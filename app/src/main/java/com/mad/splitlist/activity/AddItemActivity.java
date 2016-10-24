package com.mad.splitlist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.mad.splitlist.R;
import com.mad.splitlist.util.DollarTextWatcher;
import com.mad.splitlist.util.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Users can add an item to their current list by inputting name, quantity and price.
 */
public class AddItemActivity extends AppCompatActivity {

    public static final String ITEM_NAME_KEY = "itemname";
    public static final String ITEM_PRICE_KEY = "itemprice";
    public static final String ITEM_QUANTITY_KEY = "itemquantity";

    @BindView(R.id.activity_add_item_til_item_name)
    TextInputLayout mTilItemName;
    @BindView(R.id.activity_add_item_til_price)
    TextInputLayout mTilPrice;
    @BindView(R.id.activity_add_item_til_quantity)
    TextInputLayout mTilQuantity;

    private String mItemName;
    private String mPrice;
    private String mQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_add_item_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Max value user can enter is $999,999.99.
        EditText etPrice = mTilPrice.getEditText();
        // Shows user input in price as dollars.
        etPrice.addTextChangedListener(new DollarTextWatcher(etPrice));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu on the toolbar.
        getMenuInflater().inflate(R.menu.add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Goes back to ListContentActivity.
            case android.R.id.home:
                finish();
                return true;
            // Validate and send results back to ListContentActivity.
            case R.id.action_done:
                mItemName = mTilItemName.getEditText().getText().toString().trim();
                mPrice = mTilPrice.getEditText().getText().toString();
                mQuantity = mTilQuantity.getEditText().getText().toString();

                if (isValidated()) {
                    sendItemResults();
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sends user input back to ListContentActivity through result, which will handle the Firebase
     * Database entry in the adapter.
     */
    private void sendItemResults() {
        int formattedPrice = Utility.dollarsToInt(mPrice);
        int formattedQuantity = Integer.parseInt(mQuantity);

        // Handle adding item in ListContentActivity.
        Intent result = new Intent();
        result.putExtra(ITEM_NAME_KEY, mItemName);
        result.putExtra(ITEM_PRICE_KEY, formattedPrice);
        result.putExtra(ITEM_QUANTITY_KEY, formattedQuantity);

        setResult(RESULT_OK, result);
        finish();
    }

    /**
     * Returns whether the user input is valid so that item can be added.
     */
    private boolean isValidated() {
        boolean isValid = true;

        if (!isValidItemName()) {
            isValid = false;
        }

        if (!isValidPrice()) {
            isValid = false;
        }

        if (!isValidQuantity()) {
            isValid = false;
        }


        return isValid;
    }

    /**
     * Returns whether item name input is a valid type and produce the appropriate error messages
     * if necessary.
     */
    private boolean isValidItemName() {
        if (mItemName.isEmpty()) {
            mTilItemName.setError(getString(R.string.error_no_item_name));
            return false;
        }

        mTilItemName.setError(null);
        return true;
    }

    /**
     * Returns whether price input is a valid type and produce the appropriate error messages if
     * necessary.
     */
    private boolean isValidPrice() {
        if (mPrice.isEmpty()) {
            mTilPrice.setError(getString(R.string.error_no_price));
            return false;
        }

        mTilPrice.setError(null);
        return true;
    }

    /**
     * Returns whether quantity input is a valid type and produce the appropriate error messages if
     * necessary.
     */
    private boolean isValidQuantity() {
        if (mQuantity.isEmpty()) {
            mTilQuantity.setError(getString(R.string.error_no_quantity));
            return false;
        }

        mTilQuantity.setError(null);
        return true;
    }
}
