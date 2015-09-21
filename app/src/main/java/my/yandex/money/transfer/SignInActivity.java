package my.yandex.money.transfer;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import my.yandex.money.transfer.helper.PreferencesHelper;

public class SignInActivity extends LogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (PreferencesHelper.isFirstVisit()) {
            PreferencesHelper.markFirstVisit();
            showGreetingPopup();
            return;
        }

        checkAccessTokenAvailability();
    }


    private void checkAccessTokenAvailability() {
        // TODO: if(hasAccessToken) enterPIN() else login();
    }


    private void showGreetingPopup() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.greeting)
            .setMessage(R.string.about)
            .setPositiveButton(android.R.string.ok, null)
            .setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    checkAccessTokenAvailability();
                }
            })
            .create()
            .show();
    }
}
