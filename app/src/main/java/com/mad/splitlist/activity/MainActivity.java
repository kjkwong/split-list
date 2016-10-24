package com.mad.splitlist.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.splitlist.R;
import com.mad.splitlist.data.FirebaseSplitListAdapter;
import com.mad.splitlist.data.SplitListViewHolder;
import com.mad.splitlist.model.SplitList;
import com.mad.splitlist.util.ListStateValueListener;
import com.mad.splitlist.util.SimpleItemTouchHelperCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mad.splitlist.data.FirebaseConstants.CONTENTS_PATH;
import static com.mad.splitlist.data.FirebaseConstants.LISTS_PATH;
import static com.mad.splitlist.data.FirebaseConstants.USERS_PATH;

/**
 * Users can create lists or view all their lists in the form of card. Contacts can be notified of
 * how much they have to pay.
 */
public class MainActivity extends AppCompatActivity {

    public static final String LIST_ID_KEY = "listid";
    public static final String LIST_NAME_KEY = "listname";

    @BindView(R.id.activity_main_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.activity_main_tv_empty_state)
    TextView mTvEmptyState;

    private FirebaseSplitListAdapter mAdapter;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        // Checks if there is an authenticated user logged in and redirects to LoginActivity
        // there isn't.
        if (firebaseUser == null) {
            startLoginActivity();
        } else {
            // Get the root node in Firebase and set it as a reference to traverse tree.
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userRef = rootRef.child(USERS_PATH).child(firebaseUser.getUid());
            // References used by MainActivity to be passed to adapter.
            DatabaseReference contentsRef = userRef.child(CONTENTS_PATH);
            DatabaseReference listsRef = userRef.child(LISTS_PATH);
            // Checks to see whether there are 0 lists.
            listsRef.addValueEventListener(new ListStateValueListener(mTvEmptyState));

            Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
            setSupportActionBar(toolbar);

            mAdapter = new FirebaseSplitListAdapter(SplitList.class, R.layout.split_list_card_item,
                    SplitListViewHolder.class, listsRef, this, contentsRef);

            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            // Orders items to be displayed in descending order
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mAdapter);

            // Sets swipe to delete lists from Firebase.
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(mRecyclerView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the overflow; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Goes back to MainActivity.
            case R.id.action_signout:
                signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens a dialog that allows a user to create a new list by specifying name.
     */
    @OnClick(R.id.activity_main_fab_add)
    public void addList() {
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_list_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_create_list_dialog))
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.btn_create), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etListName = (EditText) dialogView
                                .findViewById(R.id.create_list_et_list_name);
                        String listName = etListName.getText().toString().trim();

                        // Validates list name input.
                        if (!listName.isEmpty()) {
                            // Calls adapter to create new list entry into Firebase.
                            mAdapter.createNewList(listName);
                            // Scrolls to the top of the RecyclerView.
                            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                        }
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }

    /**
     * Signs user out of the application, returning to LoginActivity.
     */
    private void signOut() {
        mFirebaseAuth.signOut();
        startLoginActivity();
    }

    /**
     * Start intent to go to LoginActivity.
     */
    private void startLoginActivity() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
