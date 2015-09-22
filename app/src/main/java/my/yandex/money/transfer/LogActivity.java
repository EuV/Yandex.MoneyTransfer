package my.yandex.money.transfer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.yandex.money.api.model.Error;

public class LogActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    protected void logDebug(String msg) {
        if (BuildConfig.DEBUG && msg != null) {
            Log.d(TAG, msg);
        }
    }

    protected void logDebug(Error error) {
        if (error != null) {
            logDebug(error.code);
        }
    }

    protected void logError(String msg) {
        if (msg != null) {
            Log.e(TAG, msg);
        }
    }

    protected void logError(Error error) {
        if (error != null) {
            logError(error.code);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logDebug("Call onCreate(), savedInstanceState: " + savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        logDebug("Call onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        logDebug("Call onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        logDebug("Call onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        logDebug("Call onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        logDebug("Call onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        logDebug("Call onDestroy()");
        super.onDestroy();
    }
}
