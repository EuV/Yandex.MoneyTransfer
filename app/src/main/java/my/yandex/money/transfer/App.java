package my.yandex.money.transfer;

import android.app.Application;
import android.content.Context;
import my.yandex.money.transfer.utils.Connections;
import my.yandex.money.transfer.utils.Notifications;
import my.yandex.money.transfer.utils.Preferences;

public class App extends Application {

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
    }
}
