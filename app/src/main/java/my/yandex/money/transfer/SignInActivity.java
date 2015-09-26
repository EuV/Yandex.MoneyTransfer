package my.yandex.money.transfer;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.yandex.money.api.methods.Token;
import com.yandex.money.api.net.AuthorizationCodeResponse;
import java.net.URISyntaxException;
import my.yandex.money.transfer.utils.Connections;
import my.yandex.money.transfer.utils.Notifications;
import my.yandex.money.transfer.utils.Preferences;

public class SignInActivity extends ApiRequestsActivity {
    private static final int TAP_TO_EXIT_INTERVAL = 2000;

    private long whenBackButtonWasPressed;
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
            Intent intent = new Intent(this, PinActivity.class);
            intent.putExtra(PinActivity.ACCESS_TOKEN_ENCRYPTED, encryptedToken);
            startActivity(intent);
            finish();
        }
    }


    private void authorizeInWebView() {
        if (!Connections.hasConnection()) {
            Notifications.showToUser(R.string.no_network_connection);
            return;
        }

        webView.postUrl(ApiLoader.AUTHORIZE_URI, loader.getAuthorizeParams());
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
                if (url.contains(ApiLoader.REDIRECT_URI)) {
                    webView.setVisibility(View.INVISIBLE);
                    try {
                        AuthorizationCodeResponse response = AuthorizationCodeResponse.parse(url);
                        if (response.code != null) {
                            loader.getAccessToken(response.code);
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


    @Override
    protected void onTokenLoaded(Token token) {
        if (token.accessToken != null) {
            Intent intent = new Intent(SignInActivity.this, PinActivity.class);
            intent.putExtra(PinActivity.ACCESS_TOKEN_PLAIN, token.accessToken);
            startActivity(intent);
            finish();
        } else {
            logError(token.error);
            authorizationFailed();
        }
    }


    @Override
    protected void onLoadFailed(Exception exception) {
        logDebug(exception.getMessage());
        authorizationFailed();
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
