package my.yandex.money.transfer;

import android.os.Bundle;
import android.widget.TextView;
import my.yandex.money.transfer.utils.Preferences;

public class AccountActivity extends FixRequestsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ((TextView) findViewById(R.id.login)).setText(Preferences.getLogin());
    }
}
