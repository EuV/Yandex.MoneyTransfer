package my.yandex.money.transfer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class LogActivity extends AppCompatActivity {
    private String TAG;

    protected void log(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(getTag(), msg);
        }
    }

    protected String getTag() {
        if (TAG == null) {
            TAG = this.getClass().getName();
        }
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("Call onCreate(), savedInstanceState: " + savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        log("Call onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        log("Call onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        log("Call onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        log("Call onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        log("Call onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        log("Call onDestroy()");
        super.onDestroy();
    }
}
