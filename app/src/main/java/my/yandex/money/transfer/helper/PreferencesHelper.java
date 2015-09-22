package my.yandex.money.transfer.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public final class PreferencesHelper {
    private static final String TAG = PreferencesHelper.class.getName();
    private static final String KEY_FIRST_VISIT = "first_visit";
    private static Context context;

    private PreferencesHelper() { /* */ }

    public static void init(Context a) {
        context = a;
    }


    public static boolean isFirstVisit() {
        return getPreferences().getBoolean(KEY_FIRST_VISIT, true);
    }


    public static void markFirstVisit() {
        editPreferences().putBoolean(KEY_FIRST_VISIT, false).apply();
        Log.d(TAG, "The first visit has been marked");
    }


    public static boolean hasEncryptedAccessToken() {
        // TODO
        return false;
    }


    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    private static Editor editPreferences() {
        return getPreferences().edit();
    }
}
