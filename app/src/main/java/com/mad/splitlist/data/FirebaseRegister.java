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

import android.util.Log;

import com.firebase.ui.database.BuildConfig;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.splitlist.model.User;

import static com.mad.splitlist.data.FirebaseConstants.DB_TAG;
import static com.mad.splitlist.data.FirebaseConstants.USERS_PATH;

/**
 * Contains methods that manipulate Firebase for RegisterActivity. Not static since this class is
 * is only used by one class, RegisterActivity.
 */
public class FirebaseRegister {

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mRootRef;

    public FirebaseRegister(FirebaseUser firebaseUser) {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = firebaseUser;
    }

    /**
     * Creates new user entry into Firebase and store the tvName.
     */
    public void createNewUser(User user) {
        // ID to access user's Firebase node.
        String userId = mFirebaseUser.getUid();
        DatabaseReference userRef = mRootRef.child(USERS_PATH).child(userId).getRef();

        userRef.setValue(user);

        if (BuildConfig.DEBUG) {
            Log.d(DB_TAG, "Adding " + userId + " into \"/" + USERS_PATH + "\"");
        }
    }
}
