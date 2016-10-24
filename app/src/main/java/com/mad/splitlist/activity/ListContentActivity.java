package com.mad.splitlist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.splitlist.R;
import com.mad.splitlist.data.FirebaseItemTypeAdapter;
import com.mad.splitlist.data.ItemTypeViewHolder;
import com.mad.splitlist.model.ItemType;
import com.mad.splitlist.util.DividerItemDecoration;
import com.mad.splitlist.util.ListStateValueListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mad.splitlist.activity.AddItemActivity.ITEM_NAME_KEY;
import static com.mad.splitlist.activity.AddItemActivity.ITEM_PRICE_KEY;
import static com.mad.splitlist.activity.AddItemActivity.ITEM_QUANTITY_KEY;
import static com.mad.splitlist.activity.MainActivity.LIST_ID_KEY;
import static com.mad.splitlist.activity.MainActivity.LIST_NAME_KEY;
import static com.mad.splitlist.data.FirebaseConstants.CONTENTS_PATH;
import static com.mad.splitlist.data.FirebaseConstants.LISTS_PATH;
import static com.mad.splitlist.data.FirebaseConstants.USERS_PATH;

/**
 * Users can view, add and delete items in their list.
 */
public class ListContentActivity extends AppCompatActivity {

    private final static int REQUEST_ADD_ITEM = 100;

    @BindView(R.id.activity_list_content_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.activity_list_content_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.activity_list_content_tv_empty_state)
    TextView mTvEmptyState;

    private FirebaseItemTypeAdapter mAdapter;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_content);
        ButterKnife.bind(this);

        // Intent passed by MainActivity.
        Intent intent = getIntent();
        String listId = intent.getStringExtra(LIST_ID_KEY);
        String listName = intent.getStringExtra(LIST_NAME_KEY);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        // Get the user's node and set it as a reference.
        DatabaseReference userRef = rootRef.child(USERS_PATH).child(user.getUid());
        DatabaseReference contentsRef = userRef.child(CONTENTS_PATH).child(listId);
        DatabaseReference listRef = userRef.child(LISTS_PATH).child(listId);
        // Checks to see whether there are 0 items.
        contentsRef.addValueEventListener(new ListStateValueListener(mTvEmptyState));

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_list_content_toolbar);
        toolbar.setTitle(listName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new FirebaseItemTypeAdapter(ItemType.class, R.layout.content_list_item,
                ItemTypeViewHolder.class, contentsRef, this, listRef, mCoordinatorLayout);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // Sets divider for each of the rows.
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Perform actions based on the intent received from another activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Request to add item to Firebase under Contents.
        if (requestCode == REQUEST_ADD_ITEM) {
            if (resultCode == RESULT_OK) {
                String itemName = data.getStringExtra(ITEM_NAME_KEY);
                int price = data.getIntExtra(ITEM_PRICE_KEY, 0);
                int quantity = data.getIntExtra(ITEM_QUANTITY_KEY, 0);

                mAdapter.addItem(itemName, price, quantity);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Goes back to MainActivity.
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Start intent to go to AddItemActivity with the request code REQUEST_ADD_ITEM.
     */
    @OnClick(R.id.activity_list_content_fab_add)
    public void startAddItemActivity() {
        startActivityForResult(new Intent(ListContentActivity.this, AddItemActivity.class),
                REQUEST_ADD_ITEM);
    }
}
