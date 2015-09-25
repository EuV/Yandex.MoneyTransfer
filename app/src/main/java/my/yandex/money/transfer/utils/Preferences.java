package my.yandex.money.transfer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import java.security.SecureRandom;

public final class Preferences {
    private static final String TAG = Preferences.class.getName();
    private static final String KEY_FIRST_VISIT = "first_visit";
    private static final String KEY_INITIALIZATION_VECTOR = "initialization_vector";
    private static final String KEY_BASE_KEY = "base_key";
    private static final String KEY_ENCRYPTED_ACCESS_TOKEN = "encrypted_access_token";
    private static Context context;

    private Preferences() { /* */ }

    public static void init(Context c) {
        context = c;
    }


    public static boolean isFirstVisit() {
        return getPreferences().getBoolean(KEY_FIRST_VISIT, true);
    }


    public static void markFirstVisit() {
        editPreferences().putBoolean(KEY_FIRST_VISIT, false).apply();
        Log.d(TAG, "The first visit has been marked");
    }


    public static byte[] getInitializationVector(int length) {
        return getSecurityInstance(KEY_INITIALIZATION_VECTOR, length);
    }


    public static byte[] getBaseKey(int length) {
        return getSecurityInstance(KEY_BASE_KEY, length);
    }


    private static byte[] getSecurityInstance(String key, int length) {
        String instance = getPreferences().getString(key, null);
        if (instance == null) {
            byte[] bytes = new byte[length];
            new SecureRandom().nextBytes(bytes);
            editPreferences().putString(key, Base64.encodeToString(bytes, Base64.DEFAULT)).apply();
            return bytes;
        }
        return Base64.decode(instance, Base64.DEFAULT);
    }


    public static String getEncryptedAccessToken() {
        return getPreferences().getString(KEY_ENCRYPTED_ACCESS_TOKEN, null);
    }


    public static void setEncryptedAccessToken(String token) {
        editPreferences().putString(KEY_ENCRYPTED_ACCESS_TOKEN, token).apply();
        Log.d(TAG, "Encrypted token has been saved");
    }


    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    private static Editor editPreferences() {
        return getPreferences().edit();
    }
}
