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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.BuildConfig;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mad.splitlist.R;
import com.mad.splitlist.model.ItemType;
import com.mad.splitlist.util.Utility;

import static com.mad.splitlist.data.FirebaseConstants.CONTENTS_PATH;
import static com.mad.splitlist.data.FirebaseConstants.DB_TAG;
import static com.mad.splitlist.data.FirebaseConstants.ISCHECKED_VALUE;
import static com.mad.splitlist.data.FirebaseConstants.LISTS_PATH;
import static com.mad.splitlist.data.FirebaseConstants.NUMOFITEMS_VALUE;
import static com.mad.splitlist.data.FirebaseConstants.TOTAL_VALUE;

/**
 * Adapter class that inherits from FirebaseRecyclerAdapter used by the ListContentActivity.
 */
public class FirebaseItemTypeAdapter extends FirebaseRecyclerAdapter<ItemType, ItemTypeViewHolder> {

    private final static boolean ADD_BOOL = true;
    private final static boolean MINUS_BOOL = false;

    private Context mContext;
    private DatabaseReference mContentsRef;
    private DatabaseReference mListRef;
    private DatabaseReference mDeleteItemRef;
    private CoordinatorLayout mCoordinatorLayout;

    private ItemType mDeletedItem;
    private int mNumOfItems;
    private long mListTotal;

    public FirebaseItemTypeAdapter(Class<ItemType> modelClass, int modelLayout,
                                   Class<ItemTypeViewHolder> viewHolderClass,
                                   DatabaseReference contentsRef, Context context,
                                   DatabaseReference listRef, CoordinatorLayout coordinatorLayout) {

        super(modelClass, modelLayout, viewHolderClass, contentsRef);

        mContext = context;
        mContentsRef = contentsRef;
        mListRef = listRef;
        mCoordinatorLayout = coordinatorLayout;

        addTotalEventListener();
        addNumOfItemsEventListener();
    }

    /**
     * Populate RecyclerView using data found in "/contents".
     */
    @Override
    protected void populateViewHolder(final ItemTypeViewHolder viewHolder, ItemType model,
                                      int position) {
        final String itemKey = getRef(position).getKey();
        final long itemTotal = model.total();
        long priceLong = model.getPrice();

        String itemName = model.getItemName();
        String formattedPrice = Utility.centsToDollarString(Long.toString(priceLong));
        int quantity = model.getQuantity();
        String formattedTotal = Utility.centsToDollarString(Long.toString(itemTotal));

        viewHolder.tvItemName.setText(itemName);
        viewHolder.tvPrice.setText(mContext.getString(R.string.tv_price, formattedPrice));
        viewHolder.tvQuantity.setText(mContext.getString(R.string.tv_quantity, quantity));
        viewHolder.tvTotal.setText(mContext.getString(R.string.tv_total, formattedTotal));

        viewHolder.chkSelected.setChecked(model.getIsChecked());
        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = viewHolder.chkSelected.isChecked();
                setChecked(itemKey, isChecked);
            }
        });

        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(itemKey, itemTotal);
                createUndoSnackbar();
            }
        });
    }

    /**
     * Creates a snackbar that appears when an item is deleted. Provides a function to undo
     * previous delete call by adding the item to the list again. Placed at the bottom of the
     * CoordinatorLayout of ListContentActivity.
     */
    private void createUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout,
                mContext.getString(R.string.item_content_snackbar), Snackbar.LENGTH_LONG)
                .setAction(mContext.getString(R.string.action_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long deletedItemTotal = mDeletedItem.total();
                        mDeleteItemRef.setValue(mDeletedItem);
                        updateListValues(deletedItemTotal, ADD_BOOL);
                    }
                });

        snackbar.show();
    }

    /**
     * Stores the value of isChecked, every time the checkbox is selected.
     */
    private void setChecked(String itemKey, boolean isChecked) {
        DatabaseReference itemRef = mContentsRef.child(itemKey);
        itemRef.child(ISCHECKED_VALUE).setValue(isChecked);
    }

    /**
     * Deletes the item in the Firebase Database. Key is used as positions can
     * remain static in Firebase.
     */
    private void deleteItem(String itemKey, long itemTotal) {
        mDeleteItemRef = mContentsRef.child(itemKey);
        // Only retrieves the value once every time the method is called.
        mDeleteItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDeletedItem = dataSnapshot.getValue(ItemType.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (BuildConfig.DEBUG) {
                    Log.d(DB_TAG, "deleteItem: " + databaseError);
                }
            }
        });

        mDeleteItemRef.removeValue();

        updateListValues(itemTotal, MINUS_BOOL);

        if (BuildConfig.DEBUG) {
            Log.d(DB_TAG, "Removing /" + CONTENTS_PATH + "/" + itemKey + "\"");
        }
    }

    /**
     * Adds an empty SplitList to Firebase under a unique key.
     */
    public void addItem(String itemName, int price, int quantity) {
        ItemType item = new ItemType(itemName, price, quantity);
        String itemKey = mContentsRef.push().getKey();

        mContentsRef.child(itemKey).setValue(item);

        long itemTotal = item.total();
        updateListValues(itemTotal, ADD_BOOL);

        if (BuildConfig.DEBUG) {
            Log.d(DB_TAG, "Adding " + itemName + " into \"/" + CONTENTS_PATH + "/"
                    + itemKey + "\"");
        }
    }

    /**
     * Consolidates list node updates used by addItem() and removeItem().
     */
    private void updateListValues(long itemTotal, boolean isAdd) {
        updateNumOfItems(isAdd);
        updateTotal(itemTotal, isAdd);
    }

    /**
     * Updates the numOfItems value in the list node.
     */
    private void updateNumOfItems(boolean isAdd) {
        if (isAdd) {
            mNumOfItems++;
        } else {
            mNumOfItems--;
        }

        mListRef.child(NUMOFITEMS_VALUE).setValue(mNumOfItems);

        if (BuildConfig.DEBUG) {
            Log.d(DB_TAG, "Setting " + NUMOFITEMS_VALUE + " to " + mNumOfItems + " in \"/"
                    + LISTS_PATH + "/" + mListRef.getKey() + "\"");
        }
    }

    /**
     * Updates the total value in the list node.
     */
    private void updateTotal(long itemTotal, boolean isAdd) {
        if (isAdd) {
            mListTotal += itemTotal;
        } else {
            mListTotal -= itemTotal;
        }

        mListRef.child(TOTAL_VALUE).setValue(mListTotal);

        if (BuildConfig.DEBUG) {
            Log.d(DB_TAG, "Setting " + TOTAL_VALUE + " to " + mListTotal + " in \"/" + LISTS_PATH
                    + "/" + mListRef.getKey() + "\"");
        }
    }

    /**
     * Retrieves the static numOfItems value in the lists node and updates the mNumOfItems field.
     */
    private void addNumOfItemsEventListener() {
        DatabaseReference numOfItemsRef = mListRef.child(NUMOFITEMS_VALUE).getRef();
        numOfItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object numOfItemsObj = dataSnapshot.getValue();
                // Check the object returned to determine if it is a Long.
                // Happens when the numOfItems is 0.
                if (numOfItemsObj != null) {
                    if (numOfItemsObj instanceof Long) {
                        mNumOfItems = ((Long) numOfItemsObj).intValue();
                    } else {
                        mNumOfItems = (int) numOfItemsObj;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (BuildConfig.DEBUG) {
                    Log.d(DB_TAG, "addNumOfItemsEventListener: " + databaseError);
                }
            }
        });
    }

    /**
     * Retrieves the static total value in the lists node and updates the mListTotal field.
     */
    private void addTotalEventListener() {
        DatabaseReference totalRef = mListRef.child(TOTAL_VALUE).getRef();
        totalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object totalObj = dataSnapshot.getValue();

                if (totalObj != null) {
                    mListTotal = (Long) totalObj;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (BuildConfig.DEBUG) {
                    Log.d(DB_TAG, "addTotalEventListener: " + databaseError);
                }
            }
        });
    }
}
