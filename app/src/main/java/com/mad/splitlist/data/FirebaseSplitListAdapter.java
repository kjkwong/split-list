/*
 * Copyright (C) 2016 Kenneth Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mad.splitlist.data;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.BuildConfig;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.mad.splitlist.R;
import com.mad.splitlist.activity.ListContentActivity;
import com.mad.splitlist.model.SplitList;
import com.mad.splitlist.util.ItemTouchHelperAdapter;
import com.mad.splitlist.util.Utility;

import java.util.Calendar;

import static com.mad.splitlist.activity.MainActivity.LIST_ID_KEY;
import static com.mad.splitlist.activity.MainActivity.LIST_NAME_KEY;
import static com.mad.splitlist.data.FirebaseConstants.DB_TAG;
import static com.mad.splitlist.data.FirebaseConstants.LISTS_PATH;

/**
 * Adapter class that inherits from FirebaseRecyclerAdapter used by the MainActivity.
 */
public class FirebaseSplitListAdapter extends
        FirebaseRecyclerAdapter<SplitList, SplitListViewHolder> implements ItemTouchHelperAdapter {

    private Context mContext;
    private DatabaseReference mListsRef;
    private DatabaseReference mContentsRef;

    public FirebaseSplitListAdapter(Class<SplitList> modelClass, int modelLayout,
                                    Class<SplitListViewHolder> viewHolderClass,
                                    DatabaseReference listsRef, Context context,
                                    DatabaseReference contentsRef) {

        super(modelClass, modelLayout, viewHolderClass, listsRef);

        mContext = context;
        mListsRef = listsRef;
        mContentsRef = contentsRef;
    }

    /**
     * Populate RecyclerView using data found in "/lists".
     */
    @Override
    protected void populateViewHolder(final SplitListViewHolder viewHolder, SplitList model,
                                      int position) {
        final DatabaseReference listIdRef = getRef(position);
        final String listKey = listIdRef.getKey();
        final String listName = model.getListName();
        long total = model.getTotal();
        int numOfItems = model.getNumOfItems();
        final String totalStr = Utility.centsToDollarString(Long.toString(total));

        viewHolder.tvListName.setText(listName);
        viewHolder.tvNumOfItems.setText(mContext.getString(R.string.tv_num_of_items, numOfItems));
        viewHolder.tvTotal.setText(mContext.getString(R.string.tv_total, totalStr));

        viewHolder.cardSplitList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListContentActivity(listKey, listName);
            }
        });

        viewHolder.layoutOverflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListPopupMenu(viewHolder.layoutOverflow, listKey);
            }
        });

        viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareList(listName, totalStr);
            }
        });
    }

    /**
     * Adds an empty Split List to Firebase under a unique key.
     */
    public void createNewList(String listName) {
        // Timestamp using locale of user.
        String timestamp = Calendar.getInstance().getTime().getTime() + "";
        SplitList splitList = new SplitList(listName, timestamp);
        String listKey = mListsRef.push().getKey();

        mListsRef.child(listKey).setValue(splitList);

        if (BuildConfig.DEBUG) {
            Log.d(DB_TAG, "Adding " + listKey + " into \"/" + LISTS_PATH + "\"");
        }
    }

    /**
     * Share list with someone in one of the apps that have Intent.ACTION_SHARE.
     */
    private void shareList(String listName, String totalStr) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String body = mContext.getString(R.string.share_message_body, listName, totalStr);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        mContext.startActivity(Intent.createChooser(shareIntent,
                mContext.getString(R.string.share_title)));
    }

    /**
     * Inflates popup menu for overflow button in each card.
     */
    private void showListPopupMenu(View view, final String listKey) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.splitlist_card, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_remove) {
                    removeCard(listKey);
                    return true;
                }

                return false;
            }
        });

        popup.show();
    }

    /**
     * Removes the card from the list and all items associated with list.
     */
    private void removeCard(String listKey) {
        DatabaseReference itemRef = mContentsRef.child(listKey);
        DatabaseReference listRef = mListsRef.child(listKey);

        itemRef.removeValue();
        listRef.removeValue();

        if (BuildConfig.DEBUG) {
            Log.d(DB_TAG, "Removing " + listKey + " from \"/" + LISTS_PATH + "\"");
        }
    }

    /**
     * Start intent to go to ListContentActivity. Uses listName for the title.
     */
    private void startListContentActivity(String listKey, String listName) {
        Intent intent = new Intent(mContext, ListContentActivity.class);
        intent.putExtra(LIST_ID_KEY, listKey);
        intent.putExtra(LIST_NAME_KEY, listName);
        mContext.startActivity(intent);
    }

    /**
     * Controls reordering an item on the list.
     */
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    /**
     * Controls deleting an item on the list.
     */
    @Override
    public void onItemDismiss(int position) {
        String itemKey = getRef(position).getKey();
        removeCard(itemKey);
    }
}
