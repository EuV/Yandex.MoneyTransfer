package my.yandex.money.transfer;

import android.app.Application;
import android.content.Context;
import my.yandex.money.transfer.utils.Connections;
import my.yandex.money.transfer.utils.Notifications;
import my.yandex.money.transfer.utils.Preferences;

public class App extends Application {

    // This field isn't saved anywhere and used for quick access and check PIN-code
    private static String token;

    public static void setToken(String token) {
        App.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static boolean hasToken() {
        return token != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        Context context = getApplicationContext();
        Preferences.init(context);
        Notifications.init(context);
        Connections.init(context);
        HistoryHelper.init(context);
    }
}
