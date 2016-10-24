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

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * A list overview containing several ItemTypes inside stored in a separate branch in Firebase.
 */
@IgnoreExtraProperties
public class SplitList {

    private String mListName;
    private int mNumOfItems;
    private String mTimestamp;
    private long mTotal;

    public SplitList() {
        // Default constructor for DataSnapshot.getValue(Splitlist.class).
        // Used by FirebaseSplitListAdapter.
    }

    public SplitList(String listName, String timestamp) {
        mListName = listName;
        mNumOfItems = 0;
        mTimestamp = timestamp;
        mTotal = 0;
    }

    public String getListName() {
        return mListName;
    }

    public void setListName(String listName) {
        mListName = listName;
    }

    public int getNumOfItems() {
        return mNumOfItems;
    }

    public void setNumOfItems(int numOfItems) {
        mNumOfItems = numOfItems;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public long getTotal() {
        return mTotal;
    }

    public void setTotal(long total) {
        mTotal = total;
    }
}
