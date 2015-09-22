package my.yandex.money.transfer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class LogActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    protected void logDebug(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg);
        }
    }

    protected void logError(String msg) {
        Log.e(TAG, msg);
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
