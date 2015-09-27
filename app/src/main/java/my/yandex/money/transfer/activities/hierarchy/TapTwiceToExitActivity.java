package my.yandex.money.transfer.activities.hierarchy;

import android.content.Intent;
import my.yandex.money.transfer.R;
import my.yandex.money.transfer.activities.SignInActivity;
import my.yandex.money.transfer.utils.Notifications;

public abstract class TapTwiceToExitActivity extends LogActivity {
    protected static final String EXIT = "EXIT";

    private static final int TAP_TO_EXIT_INTERVAL = 2000;

    private long whenBackButtonWasPressed;

    protected boolean shouldExit() {
        final long now = System.currentTimeMillis();
        if (now > whenBackButtonWasPressed + TAP_TO_EXIT_INTERVAL) {
            Notifications.showToUser(R.string.tap_to_exit);
            whenBackButtonWasPressed = now;
            return false;
        }
        return true;
    }

    protected void shutdownApp() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXIT, true);
        startActivity(intent);
    }
}
