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

package com.mad.splitlist.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * A type of item inside the shopping list.
 */
@IgnoreExtraProperties
public class ItemType {

    private boolean mIsChecked;
    private String mItemName;
    private int mPrice;
    private int mQuantity;

    public ItemType() {
        // Default constructor for DataSnapshot.getValue(ItemType.class).
        // Used by FirebaseItemTypeAdapter.
    }

    public ItemType(String itemName, int price, int quantity) {
        mItemName = itemName;
        mPrice = price;
        mQuantity = quantity;
        mIsChecked = false;
    }

    public boolean getIsChecked() {
        return mIsChecked;
    }

    public void setIsChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String name) {
        mItemName = name;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }


    /**
     * Return total cost of the item(s).
     */
    @Exclude
    public long total() {
        return (long)mPrice * (long)mQuantity;
    }
}
