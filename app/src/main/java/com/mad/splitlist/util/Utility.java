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

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Contains useful static methods that can be used by other classes.
 */
public class Utility {

    private static final int BIG_DECIMAL_SCALE = 2;
    private static final int BIG_DECIMAL_VAL = 100;

    /**
     * Returns whether the email is a valid format.
     */
    public static boolean isValidEmailFormat(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Format and return a given string in cents to a dollar string through converting it as a
     * BigDecimal. More precise than an int value if upper limit of int32 is reached.
     */
    public static String centsToDollarString(String centStr) {
        String formattedCentStr = centStr.replaceAll("[$,.]", "");
        // Set to 2 decimal places.
        BigDecimal centsBd = new BigDecimal(formattedCentStr).setScale(BIG_DECIMAL_SCALE,
                BigDecimal.ROUND_FLOOR)
                .divide(new BigDecimal(BIG_DECIMAL_VAL), BigDecimal.ROUND_FLOOR);
        ;

        return NumberFormat.getCurrencyInstance().format(centsBd);
    }

    /**
     * Returns an int from a dollar string.
     */
    public static int dollarsToInt(String dollar) {
        dollar = dollar.trim();

        // Empty string.
        if (dollar.length() == 0) {
            return 0;
        }

        // Strips all non-number characters that can be found in a dollar string.
        String formattedDollarStr = dollar.replaceAll("[$,.]", "");
        BigDecimal dollarBd = new BigDecimal(formattedDollarStr);

        return dollarBd.intValue();
    }
}
