package my.yandex.money.transfer.activities.hierarchy;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.yandex.money.api.exceptions.InvalidTokenException;
import com.yandex.money.api.methods.AccountInfo;
import com.yandex.money.api.methods.AuxToken;
import com.yandex.money.api.methods.IncomingTransferAccept;
import com.yandex.money.api.methods.IncomingTransferReject;
import com.yandex.money.api.methods.InstanceId;
import com.yandex.money.api.methods.OperationDetails;
import com.yandex.money.api.methods.OperationHistory;
import com.yandex.money.api.methods.ProcessExternalPayment;
import com.yandex.money.api.methods.ProcessPayment;
import com.yandex.money.api.methods.RequestExternalPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.methods.Token;
import my.yandex.money.transfer.ApiLoader;
import my.yandex.money.transfer.App;
import my.yandex.money.transfer.ResponseWrapper;
import my.yandex.money.transfer.activities.SignInActivity;
import my.yandex.money.transfer.utils.Preferences;

public abstract class ApiRequestsActivity extends TapTwiceToExitActivity implements LoaderManager.LoaderCallbacks<Object> {
    private final String KEY_LAST_RESPONSE_HASH = TAG + ".KEY_LAST_RESPONSE_HASH";

    private long lastResponseHash;
    protected ApiLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            lastResponseHash = savedInstanceState.getLong(KEY_LAST_RESPONSE_HASH);
        }

        loader = (ApiLoader) getLoaderManager().initLoader(0, null, this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        loader.deliverLost();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_LAST_RESPONSE_HASH, lastResponseHash);
    }


    protected void logOut() {
        logOut(true);
    }


    private void logOut(boolean revoke) {
        if (revoke) loader.revokeToken();
        deleteLocalTokenCopy();
        goToSignIn();
    }


    private void deleteLocalTokenCopy() {
        Preferences.setEncryptedAccessToken(null);
        App.setToken(null);
    }


    private void goToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new ApiLoader(this);
    }


    @Override
    public void onLoaderReset(Loader<Object> loader) { /* */ }


    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        if (!(data instanceof ResponseWrapper)) throw new IllegalArgumentException("ResponseWrapper is required!");
        ResponseWrapper wrapper = (ResponseWrapper) data;

        // Don't handle the response has previously been processed
        // (since this method is called, for example, when orientation is changed)
        if (lastResponseHash == wrapper.hash) return;
        lastResponseHash = wrapper.hash;

        Object r = wrapper.response;
        if (r instanceof InvalidTokenException) logOut(false);
        else if (r instanceof Exception) onLoadFailed((Exception) r);
        else if (r instanceof AccountInfo) onAccountInfoLoaded((AccountInfo) r);
        else if (r instanceof AuxToken) onAuxTokenLoaded((AuxToken) r);
        else if (r instanceof IncomingTransferAccept) onIncomingTransferAcceptLoaded((IncomingTransferAccept) r);
        else if (r instanceof IncomingTransferReject) onIncomingTransferRejectLoaded((IncomingTransferReject) r);
        else if (r instanceof InstanceId) onInstanceIdLoaded((InstanceId) r);
        else if (r instanceof OperationDetails) onOperationDetailsLoaded((OperationDetails) r);
        else if (r instanceof OperationHistory) onOperationHistoryLoaded((OperationHistory) r);
        else if (r instanceof ProcessExternalPayment) onProcessExternalPaymentLoaded((ProcessExternalPayment) r);
        else if (r instanceof ProcessPayment) onProcessPaymentLoaded((ProcessPayment) r);
        else if (r instanceof RequestExternalPayment) onRequestExternalPaymentLoaded((RequestExternalPayment) r);
        else if (r instanceof RequestPayment) onRequestPaymentLoaded((RequestPayment) r);
        else if (r instanceof Token) onTokenLoaded((Token) r);
        else onObjectLoaded(r);
    }

    protected void onLoadFailed(Exception exception) { /* Stub! */ }

    protected void onAccountInfoLoaded(AccountInfo accountInfo) { /* Stub! */ }

    protected void onAuxTokenLoaded(AuxToken auxToken) { /* Stub! */ }

    protected void onIncomingTransferAcceptLoaded(IncomingTransferAccept incomingTransferAccept) { /* Stub! */ }

    protected void onIncomingTransferRejectLoaded(IncomingTransferReject incomingTransferReject) { /* Stub! */ }

    protected void onInstanceIdLoaded(InstanceId instanceId) { /* Stub! */ }

    protected void onOperationDetailsLoaded(OperationDetails operationDetails) { /* Stub! */ }

    protected void onOperationHistoryLoaded(OperationHistory operationHistory) { /* Stub! */ }

    protected void onProcessExternalPaymentLoaded(ProcessExternalPayment processExternalPayment) { /* Stub! */ }

    protected void onProcessPaymentLoaded(ProcessPayment processPayment) { /* Stub! */ }

    protected void onRequestExternalPaymentLoaded(RequestExternalPayment requestExternalPayment) { /* Stub! */ }

    protected void onRequestPaymentLoaded(RequestPayment requestPayment) { /* Stub! */ }

    protected void onTokenLoaded(Token token) { /* Stub! */ }

    protected void onObjectLoaded(Object o) {
        logError("Unexpected object has been received: " + o);
    }
}
