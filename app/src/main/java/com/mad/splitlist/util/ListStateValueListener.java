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

package com.mad.splitlist.util;

import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.mad.splitlist.data.FirebaseConstants.DB_TAG;

/**
 * ValueEventListener that checks for whether there are items in the list and changes the
 * visibility of an empty state view.
 */
public class ListStateValueListener implements ValueEventListener {

    private View mViewEmptyState;

    public ListStateValueListener(View viewEmptyState) {
        mViewEmptyState = viewEmptyState;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            mViewEmptyState.setVisibility(View.VISIBLE);
        } else {
            mViewEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(DB_TAG, "ListStateValueListener: " + databaseError);
    }
}
