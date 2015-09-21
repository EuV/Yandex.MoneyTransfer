package my.yandex.money.transfer;

import android.os.Bundle;
import my.yandex.money.transfer.helper.PreferencesHelper;

public class StartActivity extends LogActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (PreferencesHelper.isFirstVisit()) {
            log("First time in app");
            PreferencesHelper.markFirstVisit();

            // TODO: showGreeting();
        }

        // TODO: if(hasAccessToken) enterPIN() else login();
    }
}
