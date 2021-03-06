package my.yandex.money.transfer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.yandex.money.api.methods.AccountInfo;
import java.net.ConnectException;
import my.yandex.money.transfer.App;
import my.yandex.money.transfer.R;
import my.yandex.money.transfer.activities.hierarchy.ApiRequestsActivity;
import my.yandex.money.transfer.activities.hierarchy.SecurityActivity;
import my.yandex.money.transfer.utils.Crypto;
import my.yandex.money.transfer.utils.Notifications;
import my.yandex.money.transfer.utils.Preferences;

import static my.yandex.money.transfer.activities.PinActivity.State.CONFIRM_PIN;
import static my.yandex.money.transfer.activities.PinActivity.State.CREATE_PIN;
import static my.yandex.money.transfer.activities.PinActivity.State.PROVIDE_PIN;


public class PinActivity extends ApiRequestsActivity {
    private static final String TAG = PinActivity.class.getName();

    private static final String KEY_STATE = TAG + ".STATE";
    private static final String KEY_LABEL = TAG + ".LABEL";
    private static final String KEY_PIN = TAG + ".PIN";

    public static final String ACCESS_TOKEN_PLAIN = TAG + ".PLAIN";
    public static final String ACCESS_TOKEN_ENCRYPTED = TAG + ".ENCRYPTED";

    private static final int MIN_PIN_LENGTH = 4;
    private static final int MAX_PIN_LENGTH = 16;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    private String plainToken;
    private String encryptedToken;
    private String pin;

    private State state;
    private TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        label = (TextView) findViewById(R.id.pin_label);
        findViewById(R.id.pin_ok).setOnClickListener(new OkOnClickListener());

        // In this case all the data will be handled via onRestoreInstanceState()
        if (savedInstanceState != null) return;

        plainToken = getIntent().getStringExtra(ACCESS_TOKEN_PLAIN);
        if (plainToken != null) {
            changeState(CREATE_PIN);
            return;
        }

        encryptedToken = getIntent().getStringExtra(ACCESS_TOKEN_ENCRYPTED);
        if (encryptedToken != null) {
            changeState(PROVIDE_PIN);
            return;
        }

        throw new IllegalArgumentException("Encrypted or plain access token must be specified when starting " + TAG);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACCESS_TOKEN_ENCRYPTED, encryptedToken);
        outState.putString(ACCESS_TOKEN_PLAIN, plainToken);
        outState.putSerializable(KEY_STATE, state);
        outState.putCharSequence(KEY_LABEL, label.getText());
        outState.putString(KEY_PIN, pin);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        encryptedToken = savedInstanceState.getString(ACCESS_TOKEN_ENCRYPTED);
        plainToken = savedInstanceState.getString(ACCESS_TOKEN_PLAIN);
        state = (State) savedInstanceState.getSerializable(KEY_STATE);
        label.setText(savedInstanceState.getCharSequence(KEY_LABEL));
        pin = savedInstanceState.getString(KEY_PIN);
    }


    private class OkOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String userPin = ((EditText) findViewById(R.id.pin_editor)).getText().toString();
            if (userPin.length() < MIN_PIN_LENGTH) {
                Notifications.showToUser(R.string.pin_min_length, MIN_PIN_LENGTH);
                return;
            }
            if (MAX_PIN_LENGTH < userPin.length()) {
                Notifications.showToUser(R.string.pin_max_length, MAX_PIN_LENGTH);
                return;
            }

            switch (state) {
                case CREATE_PIN:
                    pin = userPin;
                    changeState(CONFIRM_PIN);
                    break;

                case CONFIRM_PIN:
                    if (pin.equals(userPin)) {
                        if (encryptAndSave(plainToken, pin)) {
                            App.setToken(plainToken);
                            Preferences.resetPinCodeFailedAttempts();
                            Intent intent = new Intent(PinActivity.this, AccountActivity.class);
                            intent.putExtra(SecurityActivity.NO_SECURITY_CHECK, true);
                            startActivity(intent);
                            finish();
                        } else {
                            Notifications.showToUser(R.string.can_not_log_in);
                        }
                    } else {
                        changeState(CREATE_PIN);
                        Notifications.showToUser(R.string.pins_do_not_match);
                    }
                    break;

                case PROVIDE_PIN:
                    decryptAndCheck(encryptedToken, userPin);
                    break;
            }
        }
    }


    private boolean encryptAndSave(String plainToken, String pin) {
        String encryptedToken = Crypto.encrypt(plainToken, pin);
        if (encryptedToken == null) {
            logError("Can't encrypt user's credentials!");
            return false;
        }

        Preferences.setEncryptedAccessToken(encryptedToken);
        return true;
    }


    /**
     * Checks whether PIN-code entered is valid by comparing decrypted token with
     * the stored one or asking the server if there is no standard plain token.
     * TODO: Add some delay to prevent brute force and improve security
     *
     * @param encryptedToken an encrypted token received from a previous activity
     * @param pin            users PIN-code
     */
    private void decryptAndCheck(String encryptedToken, String pin) {
        plainToken = Crypto.decrypt(encryptedToken, pin);

        if (plainToken == null) {
            logDebug("Decrypted token is null");
            pinIncorrect();
            return;
        }

        if (App.hasToken()) {
            if (plainToken.equals(App.getToken())) {
                logDebug("Decrypted token matches the stored one");
                pinCorrect();
            } else {
                logDebug("Decrypted token doesn't match the stored one");
                pinIncorrect();
            }
            return;
        }

        loader.checkToken(plainToken);
    }


    /**
     * If this callback is invoked it indicates that the PIN entered is correct
     * (we don't actually need an account info for now)
     */
    @Override
    protected void onAccountInfoLoaded(AccountInfo accountInfo) {
        logDebug("Decrypted token has passed remote check");
        App.setToken(plainToken);
        pinCorrect();
    }


    /**
     * If this callback is invoked it indicates that the PIN entered is wrong
     */
    @Override
    protected void onLoadFailed(Exception exception) {
        if (exception instanceof ConnectException) return;
        logDebug("Decrypted token has failed remote check");
        pinIncorrect();
    }


    private void pinCorrect() {
        Preferences.resetPinCodeFailedAttempts();
        finish();
    }


    private void pinIncorrect() {
        changeState(PROVIDE_PIN);
        Notifications.showToUser(R.string.wrong_pin);

        // No local variable is accepted here even with saving in onSaveInstanceState()
        // since the user may leave the app, thus this variable will return to zero
        if (Preferences.pinCodeFailedAttempt() == MAX_FAILED_ATTEMPTS) {
            logOut();
        }
    }


    private void changeState(State state) {
        this.state = state;
        ((EditText) findViewById(R.id.pin_editor)).setText("");

        switch (state) {
            case CREATE_PIN:
                label.setText(R.string.create_pin);
                break;

            case CONFIRM_PIN:
                label.setText(R.string.repeat_pin);
                break;

            case PROVIDE_PIN:
                label.setText(R.string.enter_pin);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (shouldExit()) {
            shutdownApp();
        }
    }


    enum State {
        CREATE_PIN,
        CONFIRM_PIN,
        PROVIDE_PIN
    }
}
