package my.yandex.money.transfer.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public final class Notifications {
    private static Context context;
    private static Handler handler;

    private Notifications() { /* */ }

    public static void init(Context c) {
        context = c;
        handler = new Handler(context.getMainLooper());
    }

    public static void showToUser(final int resId) {
        showToUser(context.getString(resId));
    }

    public static void showToUser(final int resId, final Object... args) {
        showToUser(String.format(context.getString(resId), args));
    }

    public static void showToUser(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void runOnUiThread(Runnable r) {
        handler.post(r);
    }
}
