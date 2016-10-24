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
 * Details of the current user details for easy access in LoginActivity and RegisterActivity.
 */
@IgnoreExtraProperties
public class User {
    private String mName;
    private String mEmail;
    private String mPassword;

    public User(String email, String password) {
        mEmail = email;
        mPassword = password;
    }

    public User(String name, String email, String password) {
        mName = name;
        mEmail = email;
        mPassword = password;
    }

    public String getName() {
        return mName;
    }

    public void setFullName(String name) {
        mName = name;
    }

    @Exclude
    public String getEmail() {
        return mEmail;
    }

    @Exclude
    public String getPassword() {
        return mPassword;
    }
}
