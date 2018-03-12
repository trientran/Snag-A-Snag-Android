package com.digitalnoir.snagasnag.utility;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.digitalnoir.snagasnag.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Troy on 3/5/2018.
 */

public class TextValidation {

    // an array of days in 1 month for formatting the day of the month with "th", "st"...
    private static String[] suffixes =
            {"0th", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th",
                    "10th", "11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th",
                    "20th", "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th",
                    "30th", "31st"};

    /**
     * Helper method to validate some common input text (username, sizzle name...) with pattern "[A-Za-z0-9 _]+"
     */
    public static String validateTextWithPattern(Context context, EditText inputText, String fieldName) {

        // first get value of input text and clear redundant space
        String text = String.valueOf(inputText.getText());
        text = text.trim().replace("  ", " ");

        // use this Regular Expression pattern to validate if the text contain any special character
        Pattern pattern = Pattern.compile("[A-Za-z0-9 _]+");
        boolean valid = (text != null) && pattern.matcher(text).matches();

        // validate if the text is empty
        if (TextUtils.isEmpty(text)) {

            text = null;
            Toast.makeText(context, fieldName + " " + context.getString(R.string.toast_text_empty), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, getString(R.string.toast_username_empty), Toast.LENGTH_SHORT).show();
        }

        // validate if the text contain special character
        else if (!valid) {

            text = null;
            Toast.makeText(context, fieldName + " " + context.getString(R.string.toast_invalid_character), Toast.LENGTH_SHORT).show();
        } else {

            return text;
        }

        return text;
    }

    /**
     * Helper method to validate empty input text (allowing special character) (applied to address...)
     */
    public static String validateEmptyText(Context context, EditText inputText, String fieldName) {

        // first get value of input text and clear redundant space
        String text = String.valueOf(inputText.getText());
        text = text.trim().replace("  ", " ");

        // validate if the text is empty
        if (TextUtils.isEmpty(text)) {

            text = null;
            Toast.makeText(context, fieldName + " " + context.getString(R.string.toast_text_empty), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, getString(R.string.toast_username_empty), Toast.LENGTH_SHORT).show();
        }

        return text;
    }

    public static String formatCommentDateTime(String datetime) throws ParseException {

        String niceDateStr;

        // parse input time
        SimpleDateFormat fmtIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        Date date = fmtIn.parse(datetime);
        long timeInMilliseconds = date.getTime();
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeInMilliseconds);

        // get time now
        Calendar now = Calendar.getInstance();

        // if the input Date is before yesterday, then datetime output will look like: "31st Dec"
        boolean condition1 = now.get(Calendar.YEAR) - inputTime.get(Calendar.YEAR) >= 1; // compare years
        boolean condition2 = now.get(Calendar.DATE) - inputTime.get(Calendar.DATE) >= 2; // compare days of month
        if (condition1 || condition2) {

            // manually format date output with datetime pattern
            SimpleDateFormat fmtMonthOut = new SimpleDateFormat("MMM", Locale.UK);
            niceDateStr = formatDayWithOrdinalIndicator(date) + " " + fmtMonthOut.format(date);
        }
        else {

        // automatically display Facebook-like datetime value (eg 20 mins ago, yesterday...)
            niceDateStr = String.valueOf(DateUtils.getRelativeTimeSpanString(date.getTime(),
                    Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));
        }
        LogUtil.debug("troe", niceDateStr);
        return niceDateStr;
    }

    private static String formatDayWithOrdinalIndicator(Date date) {

        SimpleDateFormat formatDayOfMonth = new SimpleDateFormat("d", Locale.UK);
        int day = Integer.parseInt(formatDayOfMonth.format(date));

        return suffixes[day];
    }
}
