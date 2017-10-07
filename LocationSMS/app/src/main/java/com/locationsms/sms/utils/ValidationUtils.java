package com.locationsms.sms.utils;

import android.widget.EditText;

/**
 * Created by Javed.Salat on 5/22/2016.
 */
public class ValidationUtils {


    public static boolean hasText(EditText editText, String errorMsg) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(errorMsg);
            return false;
        }

        return true;
    }
}
