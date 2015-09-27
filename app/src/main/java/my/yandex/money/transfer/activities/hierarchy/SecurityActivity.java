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

    /**
     * Time when the user leaves an activity.
     * Should be the only for entire application to prevent
     * PIN-code check when switching between activities.
     */
    private static long leavingTime;

    /**
     * Prevents from looping between SecurityActivity descendants and PinActivity
     */
    private boolean isAlreadyChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            leavingTime = savedInstanceState.getLong(STOP_TIME);
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

        if (isAlreadyChecked) {
            isAlreadyChecked = false;
            return;
        }

        long now = System.currentTimeMillis();
        if (now - leavingTime > ALLOWED_AFK_INTERVAL) {
            Intent intent = new Intent(this, PinActivity.class);
            intent.putExtra(PinActivity.ACCESS_TOKEN_ENCRYPTED, Preferences.getEncryptedAccessToken());
            startActivity(intent);
            isAlreadyChecked = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        leavingTime = System.currentTimeMillis();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STOP_TIME, leavingTime);
        outState.putBoolean(IS_ALREADY_CHECKED, isAlreadyChecked);
    }
}
