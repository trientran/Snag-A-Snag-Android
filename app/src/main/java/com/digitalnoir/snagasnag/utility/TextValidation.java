package com.digitalnoir.snagasnag.utility;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.digitalnoir.snagasnag.R;

import java.util.regex.Pattern;

/**
 * Created by Troy on 3/5/2018.
 */

public class TextValidation {

    /**
     * Helper method to validate some common input text (username, sizzle name...)
     */
    public static String validateCommonInputText(Context context, EditText inputText, int toastMsgId) {

        // first get value of input text and clear redundant space
        String text = String.valueOf(inputText.getText());
        text = text.trim().replace("  ", " ");

        // use this Regular Expression pattern to validate if the text contain any special character
        Pattern pattern = Pattern.compile("[A-Za-z0-9 _]+");
        boolean valid = (text != null) && pattern.matcher(text).matches();

        // validate if the text is empty
        if (TextUtils.isEmpty(text)) {

            text = null;
            Toast.makeText(context, context.getString(toastMsgId), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, getString(R.string.toast_username_empty), Toast.LENGTH_SHORT).show();
        }

        // validate if the text contain special character
        else if (!valid) {

            text = null;
            Toast.makeText(context, context.getString(R.string.toast_invalid_character), Toast.LENGTH_SHORT).show();
        }

        else {

            return text;
        }

        return text;
    }
}
