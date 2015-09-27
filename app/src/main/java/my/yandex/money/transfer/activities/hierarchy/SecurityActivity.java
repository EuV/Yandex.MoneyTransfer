package my.yandex.money.transfer.activities.hierarchy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import my.yandex.money.transfer.activities.PinActivity;
import my.yandex.money.transfer.utils.Preferences;

public abstract class SecurityActivity extends ApiRequestsActivity {
    public static final String NO_SECURITY_CHECK = "NO_SECURITY_CHECK";

    private static final long ALLOWED_AFK_INTERVAL = 5 * 1000;
    private final String STOP_TIME = TAG + ".STOP_TIME";
    private final String IS_ALREADY_CHECKED = TAG + ".IS_ALREADY_CHECKED";

    private long stopTime;
    private boolean isAlreadyChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            stopTime = savedInstanceState.getLong(STOP_TIME);
            isAlreadyChecked = savedInstanceState.getBoolean(IS_ALREADY_CHECKED);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (getIntent().getBooleanExtra(NO_SECURITY_CHECK, false)) {
            getIntent().removeExtra(NO_SECURITY_CHECK);
            return;
        }

        // Prevents from looping between *Activity and PinActivity
        if (isAlreadyChecked) {
            isAlreadyChecked = false;
            return;
        }

        long now = System.currentTimeMillis();
        if (now - stopTime > ALLOWED_AFK_INTERVAL) {
            Intent intent = new Intent(this, PinActivity.class);
            intent.putExtra(PinActivity.ACCESS_TOKEN_ENCRYPTED, Preferences.getEncryptedAccessToken());
            startActivity(intent);
            isAlreadyChecked = true;
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        stopTime = System.currentTimeMillis();
        outState.putLong(STOP_TIME, stopTime);
        outState.putBoolean(IS_ALREADY_CHECKED, isAlreadyChecked);
    }
}
