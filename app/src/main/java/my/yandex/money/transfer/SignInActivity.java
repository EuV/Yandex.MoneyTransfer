package my.yandex.money.transfer;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.yandex.money.api.methods.Token;
import com.yandex.money.api.model.Scope;
import com.yandex.money.api.net.AuthorizationCodeResponse;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Authorization.Params;
import com.yandex.money.api.net.OAuth2Session;
import com.yandex.money.api.net.OnResponseReady;
import java.io.IOException;
import java.net.URISyntaxException;
import my.yandex.money.transfer.utils.Connections;
import my.yandex.money.transfer.utils.Notifications;
import my.yandex.money.transfer.utils.Preferences;

public class SignInActivity extends LogActivity {
    private static final String CLIENT_ID = "DE70875A6C42ACAA9BB53B5B56F8A7D1C686F8975E0DA5B7CE2C8B2876BAF214";
    private static final String REDIRECT_URI = "http://yandex.money-transfer.ru/process";
    private static final String AUTHORIZE_URI = "https://m.money.yandex.ru/oauth/authorize";

    private static final int TAP_TO_EXIT_INTERVAL = 2000;

    private OAuth2Session session = new OAuth2Session(new DefaultApiClient(CLIENT_ID));
    private ProgressBar progressBar;
    private WebView webView;

    private long whenBackButtonWasPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setupViews();

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
            return;
        }

        if (Preferences.isFirstVisit()) {
            Preferences.markFirstVisit();
            showGreetingPopup();
            return;
        }

        signIn();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_in_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            authorizeInWebView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        } else {
            final long now = System.currentTimeMillis();
            if (now > whenBackButtonWasPressed + TAP_TO_EXIT_INTERVAL) {
                Notifications.showToUser(R.string.tap_to_exit);
                whenBackButtonWasPressed = now;
                return;
            }
        }
        super.onBackPressed();
    }


    private void signIn() {
        String encryptedToken = Preferences.getEncryptedAccessToken();
        if (encryptedToken == null) {
            authorizeInWebView();
        } else {
            Intent intent = new Intent(this, PINActivity.class);
            intent.putExtra(PINActivity.ACCESS_TOKEN_ENCRYPTED, encryptedToken);
            startActivity(intent);
            finish();
        }
    }


    private void authorizeInWebView() {
        if (!Connections.hasConnection()) {
            Notifications.showToUser(R.string.no_network_connection);
            return;
        }

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
        webView.setVisibility(View.VISIBLE);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void setupViews() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

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
                if (url.contains(REDIRECT_URI)) {
                    webView.setVisibility(View.INVISIBLE);
                    try {
                        AuthorizationCodeResponse response = AuthorizationCodeResponse.parse(url);
                        if (response.code != null) {
                            getAccessToken(response.code);
                        } else {
                            logDebug(response.error);
                            Notifications.showToUser(R.string.access_denied);
                            authorizeInWebView();
                        }
                    } catch (URISyntaxException e) {
                        logError(e.getMessage());
                        Notifications.showToUser(R.string.error_in_server_response);
                    }
                }
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


    private void getAccessToken(String authorizationCode) {
        try {
            session.enqueue(new Token.Request(authorizationCode, CLIENT_ID, REDIRECT_URI), new OnResponseReady<Token>() {

                @Override
                public void onFailure(Exception exception) {
                    logDebug(exception.getMessage());
                    authorizationFailed();
                }

                @Override
                public void onResponse(Token token) {
                    if (token.accessToken != null) {
                        session.setAccessToken(token.accessToken);
                        Intent intent = new Intent(SignInActivity.this, PINActivity.class);
                        intent.putExtra(PINActivity.ACCESS_TOKEN_PLAIN, token.accessToken);
                        startActivity(intent);
                        finish();
                    } else {
                        logError(token.error);
                        authorizationFailed();
                    }
                }
            });
        } catch (IOException e) {
            logError(e.getMessage());
            authorizationFailed();
        }
    }


    private void authorizationFailed() {
        Notifications.showToUser(R.string.authorization_failed);
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
