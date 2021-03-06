package my.yandex.money.transfer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.yandex.money.api.methods.AccountInfo;
import com.yandex.money.api.model.AccountStatus;
import com.yandex.money.api.model.BalanceDetails;
import com.yandex.money.api.utils.Strings;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import my.yandex.money.transfer.App;
import my.yandex.money.transfer.R;
import my.yandex.money.transfer.activities.hierarchy.SecurityActivity;
import my.yandex.money.transfer.utils.Preferences;

public class AccountActivity extends SecurityActivity implements OnRefreshListener {
    private static final Map<AccountStatus, Integer> ACCOUNT_STATUS_MAP = new HashMap<>();

    static {
        ACCOUNT_STATUS_MAP.put(AccountStatus.ANONYMOUS, R.string.account_status_anonymous);
        ACCOUNT_STATUS_MAP.put(AccountStatus.NAMED, R.string.account_status_named);
        ACCOUNT_STATUS_MAP.put(AccountStatus.IDENTIFIED, R.string.account_status_identified);
    }

    private final String KEY_LOGIN = TAG + ".LOGIN";
    private final String KEY_ACCOUNT_NUMBER = TAG + ".ACCOUNT_NUMBER";
    private final String KEY_ACCOUNT_STATUS = TAG + ".ACCOUNT_STATUS";
    private final String KEY_BALANCE = TAG + ".BALANCE";
    private final String KEY_BALANCE_AVAILABLE = TAG + ".BALANCE_AVAILABLE";

    private TextView login;
    private TextView accountNumber;
    private TextView accountStatus;
    private TextView balance;
    private TextView balanceAvailable;
    private SwipeRefreshLayout refresher;

    private boolean isAlreadyLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        setupViews();

        if (savedInstanceState == null) {
            login.setText(Preferences.getLogin());
            if (App.hasToken()) initialLoading();
            return;
        }

        login.setText(savedInstanceState.getCharSequence(KEY_LOGIN));
        accountNumber.setText(savedInstanceState.getCharSequence(KEY_ACCOUNT_NUMBER));
        accountStatus.setText(savedInstanceState.getCharSequence(KEY_ACCOUNT_STATUS));
        balance.setText(savedInstanceState.getCharSequence(KEY_BALANCE));
        balanceAvailable.setText(savedInstanceState.getCharSequence(KEY_BALANCE_AVAILABLE));
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (balance.getText().toString().isEmpty() && App.hasToken()) {
            initialLoading();
        }
    }


    private void initialLoading() {
        if (isAlreadyLoaded) return;
        isAlreadyLoaded = true;
        loader.getAccountInfo();
        refresher.setRefreshing(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_transfer:
                startActivity(TransferActivity.class);
                return true;

            case R.id.action_history:
                startActivity(HistoryActivity.class);
                return true;

            case R.id.action_contacts:
                startActivity(ContactsActivity.class);
                return true;

            case R.id.action_goods_and_services:
                startActivity(GoodsAndServicesActivity.class);
                return true;

            case R.id.action_exit:
                logOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void startActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_LOGIN, login.getText());
        outState.putCharSequence(KEY_ACCOUNT_NUMBER, accountNumber.getText());
        outState.putCharSequence(KEY_ACCOUNT_STATUS, accountStatus.getText());
        outState.putCharSequence(KEY_BALANCE, balance.getText());
        outState.putCharSequence(KEY_BALANCE_AVAILABLE, balanceAvailable.getText());
    }


    private void setupViews() {
        login = (TextView) findViewById(R.id.login);
        accountNumber = (TextView) findViewById(R.id.account_number);
        accountStatus = (TextView) findViewById(R.id.account_status);
        balance = (TextView) findViewById(R.id.balance_value);
        balanceAvailable = (TextView) findViewById(R.id.balance_available_value);
        refresher = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresher.setOnRefreshListener(this);
        findViewById(R.id.button_transfer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(TransferActivity.class);
            }
        });
    }


    @Override
    protected void onLoadFailed(Exception exception) {
        refresher.setRefreshing(false);
    }


    @Override
    protected void onAccountInfoLoaded(AccountInfo accountInfo) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        balanceAvailable.setText(numberFormat.format(getBalanceAvailable(accountInfo)));
        balance.setText(numberFormat.format(accountInfo.balance));
        accountNumber.setText(Strings.concatenate(Strings.split(accountInfo.account, 4), " "));
        accountStatus.setText(getString(ACCOUNT_STATUS_MAP.get(accountInfo.accountStatus)));
        refresher.setRefreshing(false);
    }


    private BigDecimal getBalanceAvailable(AccountInfo accountInfo) {
        BigDecimal available = accountInfo.balance;
        BalanceDetails details = accountInfo.balanceDetails;
        if (details != null && details.available != null) {
            available = details.available;
        }
        return available;
    }


    @Override
    public void onRefresh() {
        loader.getAccountInfo();
    }


    @Override
    public void onBackPressed() {
        if (shouldExit()) {
            shutdownApp();
        }
    }
}
