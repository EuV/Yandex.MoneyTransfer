package my.yandex.money.transfer;

import android.content.Context;
import android.content.Loader;
import android.os.Handler;
import android.os.Looper;
import com.yandex.money.api.methods.AccountInfo;
import com.yandex.money.api.methods.OperationHistory;
import com.yandex.money.api.methods.ProcessPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.methods.Token;
import com.yandex.money.api.methods.params.P2pTransferParams;
import com.yandex.money.api.model.Scope;
import com.yandex.money.api.net.ApiRequest;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Session;
import com.yandex.money.api.net.OnResponseReady;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import my.yandex.money.transfer.utils.Connections;
import my.yandex.money.transfer.utils.Notifications;

public class ApiLoader extends Loader<Object> {
    public static final String CLIENT_ID = "DE70875A6C42ACAA9BB53B5B56F8A7D1C686F8975E0DA5B7CE2C8B2876BAF214";
    public static final String REDIRECT_URI = "http://yandex.money-transfer.ru/process";
    public static final String AUTHORIZE_URI = "https://m.money.yandex.ru/oauth/authorize";
    public static final String AUTHORIZE_RESPONSE_URI = "https://money.yandex.ru/oauth2/authorize?requestid=";

    private OAuth2Session session;
    private ResponseWrapper response;
    private ApiRequest request;
    private Handler handler;

    public ApiLoader(Context context) {
        super(context);
        session = new OAuth2Session(new DefaultApiClient(CLIENT_ID));
        session.setDebugLogging(BuildConfig.DEBUG);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onForceLoad() {
        if (!Connections.hasConnection()) {
            Notifications.showToUser(R.string.no_network_connection);
            deliverResult(new ConnectException());
            return;
        }

        try {
            session.enqueue(request, new OnResponseReady() {

                @Override
                public void onFailure(final Exception exception) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            deliverResult(exception);
                        }
                    });
                }

                @Override
                public void onResponse(final Object object) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            deliverResult(object);
                        }
                    });
                }
            });
        } catch (IOException e) {
            deliverResult(e);
        }
    }


    @Override
    public void deliverResult(Object data) {
        response = new ResponseWrapper(data);
        super.deliverResult(response);
    }


    /**
     * Used for receiving the data was loaded when listener Activity was stopped
     */
    public void deliverLost() {
        if (response != null) {
            super.deliverResult(response);
        }
    }


    public byte[] getAuthorizeParams() {
        return session.createOAuth2Authorization().getAuthorizeParams()
            .setResponseType("code")
            .setRedirectUri(REDIRECT_URI)
            .addScope(Scope.ACCOUNT_INFO)
            .addScope(Scope.OPERATION_HISTORY)
            .addScope(Scope.OPERATION_DETAILS)
            .addScope(Scope.INCOMING_TRANSFERS)
            .addScope(Scope.PAYMENT_P2P)
            .build();
    }


    public void checkToken(String token) {
        request = new AccountInfo.Request();
        session.setAccessToken(token);
        forceLoad();
    }


    public void getAccountInfo() {
        request = new AccountInfo.Request();
        load();
    }


    public void getAccessToken(String authorizationCode) {
        request = new Token.Request(authorizationCode, CLIENT_ID, REDIRECT_URI);
        load();
    }


    public void revokeToken() {
        request = new Token.Revoke();
        load();
    }


    public void requestPayment(String to, BigDecimal amountDue, String message, boolean codepro, Integer expirePeriod) {
        P2pTransferParams.Builder builder = new P2pTransferParams.Builder(to)
            .setAmountDue(amountDue)
            .setMessage(message)
            .setCodepro(codepro)
            .setExpirePeriod(expirePeriod);
        request = RequestPayment.Request.newInstance(builder.build());
        load();
    }


    public void processPayment(String requestId) {
        request = new ProcessPayment.Request(requestId);
        load();
    }


    public void getOperationHistory() {
        getOperationHistory("0");
    }


    public void getOperationHistory(String startRecord) {
        request = new OperationHistory.Request.Builder()
            .setDetails(true)
            .setRecords(100)
            .setStartRecord(startRecord)
            .createRequest();
        load();
    }


    private void load() {
        if (App.hasToken()) session.setAccessToken(App.getToken());
        forceLoad();
    }
}
