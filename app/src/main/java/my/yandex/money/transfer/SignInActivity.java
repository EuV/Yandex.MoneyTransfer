package my.yandex.money.transfer;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.yandex.money.api.model.Scope;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Authorization.Params;
import com.yandex.money.api.net.OAuth2Session;
import my.yandex.money.transfer.helper.PreferencesHelper;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_BACK;

public class SignInActivity extends LogActivity {
    private static final String CLIENT_ID = "DE70875A6C42ACAA9BB53B5B56F8A7D1C686F8975E0DA5B7CE2C8B2876BAF214";
    private static final String REDIRECT_URI = "http://yandex.money-transfer.ru/process";
    private static final String AUTHORIZE_URI = "https://m.money.yandex.ru/oauth/authorize";

    private ProgressBar progressBar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setupViews();

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
            return;
        }

        if (PreferencesHelper.isFirstVisit()) {
            PreferencesHelper.markFirstVisit();
            showGreetingPopup();
            return;
        }

        signIn();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((event.getAction() == ACTION_DOWN) && (keyCode == KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        // TODO: Press one more time to exit
        return super.onKeyDown(keyCode, event);
    }


    private void signIn() {
        if (PreferencesHelper.hasEncryptedAccessToken()) {
            // TODO: Redirect to PIN Activity
        } else {
            authorizeInWebView();
        }
    }


    private void authorizeInWebView() {

        // TODO: check internet connection
        OAuth2Session session = new OAuth2Session(new DefaultApiClient(CLIENT_ID));
        session.setDebugLogging(BuildConfig.DEBUG);

        Params postParams = session.createOAuth2Authorization().getAuthorizeParams()
            .setResponseType("code")
            .setRedirectUri(REDIRECT_URI)
            .addScope(Scope.ACCOUNT_INFO)
            .addScope(Scope.OPERATION_HISTORY)
            .addScope(Scope.OPERATION_DETAILS)
            .addScope(Scope.INCOMING_TRANSFERS)
            .addScope(Scope.PAYMENT_P2P);

        webView.postUrl(AUTHORIZE_URI, postParams.build());
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void setupViews() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // TODO: Refresh button
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setSavePassword(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHapticFeedbackEnabled(false);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                // TODO: Handle REDIRECT_URI
                view.loadUrl(url);
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
            }
        });
    }


    private void showGreetingPopup() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.greeting)
            .setMessage(R.string.about)
            .setPositiveButton(android.R.string.ok, null)
            .setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    signIn();
                }
            })
            .create()
            .show();
    }
}
