package my.yandex.money.transfer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static android.content.Context.CONNECTIVITY_SERVICE;

public final class Connections {
    private static final String TAG = Connections.class.getName();
    private static Context context;

    private Connections() { /* */ }

    public static void init(Context c) {
        context = c;
    }

    public static boolean hasConnection() {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            Log.d(TAG, "No network connection");
            return false;
        }
    }
}
