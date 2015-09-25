package my.yandex.money.transfer;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import my.yandex.money.transfer.utils.Crypto;
import my.yandex.money.transfer.utils.Notifications;
import my.yandex.money.transfer.utils.Preferences;

public class PINActivity extends LogActivity {
    private static final String TAG = PINActivity.class.getName();

    private static final String STATE = TAG + ".STATE";
    private static final String LABEL = TAG + ".LABEL";
    private static final String PIN = TAG + ".PIN";

    public static final String ACCESS_TOKEN_PLAIN = TAG + ".PLAIN";
    public static final String ACCESS_TOKEN_ENCRYPTED = TAG + ".ENCRYPTED";

    private static final int MIN_PIN_LENGTH = 4;
    private static final int MAX_PIN_LENGTH = 16;

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

        if (savedInstanceState != null) return;

        plainToken = getIntent().getStringExtra(ACCESS_TOKEN_PLAIN);
        if (plainToken != null) {
            state = State.CREATE_PIN;
            label.setText(R.string.create_pin);
            return;
        }

        encryptedToken = getIntent().getStringExtra(ACCESS_TOKEN_ENCRYPTED);
        if (encryptedToken != null) {
            state = State.PROVIDE_PIN;
            label.setText(R.string.enter_pin);
            return;
        }

        throw new IllegalArgumentException("Encrypted or plain access token must be specified when starting " + TAG);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACCESS_TOKEN_ENCRYPTED, encryptedToken);
        outState.putString(ACCESS_TOKEN_PLAIN, plainToken);
        outState.putSerializable(STATE, state);
        outState.putCharSequence(LABEL, label.getText());
        outState.putString(PIN, pin);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        encryptedToken = savedInstanceState.getString(ACCESS_TOKEN_ENCRYPTED);
        plainToken = savedInstanceState.getString(ACCESS_TOKEN_PLAIN);
        state = (State) savedInstanceState.getSerializable(STATE);
        label.setText(savedInstanceState.getCharSequence(LABEL));
        pin = savedInstanceState.getString(PIN);
    }


    private class OkOnClickListener implements View.OnClickListener {
        final EditText editor = (EditText) findViewById(R.id.pin_editor);

        @Override
        public void onClick(View okButton) {
            String userPin = editor.getText().toString();
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
                    label.setText(R.string.repeat_pin);
                    editor.setText("");
                    state = State.CONFIRM_PIN;
                    break;

                case CONFIRM_PIN:
                    if (pin.equals(userPin)) {
                        if (encryptAndSave(plainToken, pin)) {
                            App.setToken(plainToken);
                            goToPaymentActivity();
                        } else {
                            Notifications.showToUser(R.string.can_not_log_in);
                        }
                    } else {
                        label.setText(R.string.create_pin);
                        editor.setText("");
                        state = State.CREATE_PIN;
                        Notifications.showToUser(R.string.pins_do_not_match);
                    }
                    break;

                case PROVIDE_PIN:
                    okButton.setEnabled(false);
                    if (decryptAndCheck(encryptedToken, userPin)) {
                        goToPaymentActivity();
                    } else {
                        editor.setText("");
                        Notifications.showToUser(R.string.wrong_pin);
                        // TODO: logout
                    }
                    okButton.setEnabled(true);
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


    private boolean decryptAndCheck(String encryptedToken, String pin) {
        String plainToken = Crypto.decrypt(encryptedToken, pin);

        if (plainToken == null) {
            logDebug("Decrypted token is null");
            return false;
        }

        if (App.hasToken()) {
            if (plainToken.equals(App.getToken())) {
                logDebug("Decrypted token matches the stored one");
                return true;
            } else {
                logDebug("Decrypted token doesn't match the stored one");
                return false;
            }
        }

        if (!remoteCheckSucceed(plainToken)) {
            logDebug("Decrypted token has failed remote check");
            return false;
        }

        logDebug("Decrypted token is valid");
        App.setToken(plainToken);
        return true;
    }


    private boolean remoteCheckSucceed(String token) {
        // TODO
        return true;
    }

    private void goToPaymentActivity() {
        // TODO
    }


    private enum State {
        CREATE_PIN,
        CONFIRM_PIN,
        PROVIDE_PIN
    }
}
