package my.yandex.money.transfer.activities;

import android.os.Bundle;
import android.widget.TextView;
import my.yandex.money.transfer.R;
import my.yandex.money.transfer.activities.hierarchy.ApiRequestsActivity;
import my.yandex.money.transfer.utils.Preferences;

public class AccountActivity extends ApiRequestsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ((TextView) findViewById(R.id.login)).setText(Preferences.getLogin());
    }
}
