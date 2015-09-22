package my.yandex.money.transfer;

import android.app.Application;
import android.content.Context;
import my.yandex.money.transfer.helper.ConnectionHelper;
import my.yandex.money.transfer.helper.NotificationHelper;
import my.yandex.money.transfer.helper.PreferencesHelper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        Context context = getApplicationContext();
        PreferencesHelper.init(context);
        NotificationHelper.init(context);
        ConnectionHelper.init(context);
    }
}
