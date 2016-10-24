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

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * For setting a new OnTextChangeListener that can be used for inputting dollars to an EditText
 * field. Dollars will be inputted from right to left and the whole amount in cents is required.
 */
public class DollarTextWatcher implements TextWatcher {

    private final WeakReference<EditText> mEditText;

    public DollarTextWatcher(EditText editText) {
        mEditText = new WeakReference<>(editText);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        EditText editText = mEditText.get();

        if (editText == null) {
            return;
        }

        String s = editable.toString();
        editText.removeTextChangedListener(this);
        // Format user input to show dollars and cents rather than a number.
        String formatted = Utility.centsToDollarString(s);
        editText.setText(formatted);
        editText.setSelection(formatted.length());
        editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}
