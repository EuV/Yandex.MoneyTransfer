package my.yandex.money.transfer.helper;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public final class NotificationHelper {
    private static Context context;
    private static Handler handler;

    private NotificationHelper() { /* */ }

    public static void init(Context c) {
        context = c;
        handler = new Handler(context.getMainLooper());
    }

    public static void showToUser(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void runOnUiThread(Runnable r) {
        handler.post(r);
    }
}
