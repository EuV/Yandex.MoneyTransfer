package my.yandex.money.transfer.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.yandex.money.api.methods.OperationHistory;
import java.util.ArrayList;
import my.yandex.money.transfer.HistoryAdapter;
import my.yandex.money.transfer.HistoryHelper;
import my.yandex.money.transfer.OperationSub;
import my.yandex.money.transfer.R;
import my.yandex.money.transfer.activities.hierarchy.SecurityActivity;
import my.yandex.money.transfer.utils.Notifications;

public class HistoryActivity extends SecurityActivity implements OnRefreshListener, HistoryHelper.OnDBOperationPerformed {
    private static final String KEY_OPERATIONS = "key_operations";
    private static final String KEY_IS_ALREADY_LOADED_FROM_WEB = "key_is_already_loaded_from_web";

    private SwipeRefreshLayout refresher;
    private HistoryAdapter historyAdapter;

    private boolean isAlreadyLoadedFromWeb = false;
    private String nextRecord = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        refresher = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresher.setOnRefreshListener(this);

        historyAdapter = new HistoryAdapter();
        if (savedInstanceState == null) {
            HistoryHelper.load(this);
            animateRefreshing(true);
        } else {
            historyAdapter.setOperations(savedInstanceState.<OperationSub>getParcelableArrayList(KEY_OPERATIONS));
            isAlreadyLoadedFromWeb = savedInstanceState.getBoolean(KEY_IS_ALREADY_LOADED_FROM_WEB);
        }

        RecyclerView historyView = (RecyclerView) findViewById(R.id.history_view);
        historyView.setLayoutManager(new LinearLayoutManager(this));
        historyView.setAdapter(historyAdapter);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_OPERATIONS, historyAdapter.getOperations());
        outState.putBoolean(KEY_IS_ALREADY_LOADED_FROM_WEB, isAlreadyLoadedFromWeb);
    }


    /**
     * Called when the history of operations is loaded from db.
     * In case it contains nothing, initiates loading from web,
     * otherwise sends it to adapter.
     */
    @Override
    public void onOperationsLoadedFromDB(ArrayList<OperationSub> operations) {
        if (operations.isEmpty() && !isAlreadyLoadedFromWeb) {
            loader.getOperationHistory();
            isAlreadyLoadedFromWeb = true;
        } else {
            historyAdapter.setOperations(operations);
            animateRefreshing(false);
        }
    }


    /**
     * Called when Operations are stored in DB.
     * If there's no necessity in further downloading history,
     * initiates loading of entire ordered operations list.
     *
     * @param furtherUpdateNeeded indicates that there is no conflicts (duplicates) while storing into DB,
     *                            and therefore it's needed to continue loading from web (if possible)
     */
    @Override
    public void onOperationsStoredInDB(boolean furtherUpdateNeeded) {
        if (furtherUpdateNeeded && nextRecord != null) {
            loader.getOperationHistory(nextRecord);
        } else {
            HistoryHelper.load(this);
        }
    }


    /**
     * Called when an exception occurred while loading from web.
     */
    @Override
    protected void onLoadFailed(Exception exception) {
        loadHistoryFailed();
    }


    /**
     * Called when some part of history is loaded from web.
     */
    @Override
    protected void onOperationHistoryLoaded(OperationHistory operationHistory) {
        if (operationHistory.error == null) {
            HistoryHelper.save(this, operationHistory.operations);
            nextRecord = operationHistory.nextRecord;
        } else {
            loadHistoryFailed();
        }
    }


    private void loadHistoryFailed() {
        Notifications.showToUser(R.string.load_history_failed);
        animateRefreshing(false);
    }


    @Override
    public void onRefresh() {
        loader.getOperationHistory();
    }


    /**
     * This WA is needed since SwipeRefreshLayout tends to miss
     * direct call of setRefreshing() when activity starts.
     */
    private void animateRefreshing(final boolean refreshing) {
        refresher.post(new Runnable() {
            @Override
            public void run() {
                refresher.setRefreshing(refreshing);
            }
        });
    }
}
