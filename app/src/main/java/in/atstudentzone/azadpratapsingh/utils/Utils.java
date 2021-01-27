package in.atstudentzone.azadpratapsingh.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import in.atstudentzone.azadpratapsingh.R;

public class Utils {

    public static boolean setStringPreference(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    public static String getStringPreference(Context context, String key) {
        String value = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getString(key, null);
        }
        return value;
    }

    public static boolean setBooleanPreference(Activity activity, String key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean getBooleanPreference(Activity activity, String key) {
        boolean value = false;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        if (preferences != null) {
            value = preferences.getBoolean(key, false);
        }
        return value;
    }

    public static void showToastMessage(Activity mActivity, String strMessage, boolean warningMessage) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) mActivity.findViewById(R.id.toast_layout_root));

        ((TextView) layout.findViewById(R.id.toast_textView)).setText(strMessage);
        layout.findViewById(R.id.toast_layout_root).setBackgroundColor(mActivity.getApplicationContext().getResources().getColor(warningMessage ? (android.R.color.holo_red_light) : (R.color.primary_green)));
        Toast toast = new Toast(mActivity.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 30);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static boolean checkForDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean checkForInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean validateEditTextInput(EditText editText) {
        if(editText != null && editText.getText() != null
                && editText.getText().toString() != null
                && !editText.getText().toString().isEmpty())
        return true;
        return false;
    }
}
