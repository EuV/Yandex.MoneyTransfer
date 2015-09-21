package my.yandex.money.transfer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import my.yandex.money.transfer.helper.PreferencesHelper;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = StartActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (PreferencesHelper.isFirstVisit()) {
            Log.d(TAG, "First time in app");
            PreferencesHelper.markFirstVisit();

            // TODO: showGreeting();
        }

        // TODO: if(hasAccessToken) enterPIN() else login();
    }
}
