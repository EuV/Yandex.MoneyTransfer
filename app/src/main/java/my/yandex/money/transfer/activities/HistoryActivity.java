package my.yandex.money.transfer.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.yandex.money.api.methods.OperationHistory;
import java.net.ConnectException;
import my.yandex.money.transfer.HistoryAdapter;
import my.yandex.money.transfer.OperationDisplayed;
import my.yandex.money.transfer.R;
import my.yandex.money.transfer.activities.hierarchy.SecurityActivity;
import my.yandex.money.transfer.utils.Notifications;

public class HistoryActivity extends SecurityActivity implements OnRefreshListener {
    private static final String KEY_OPERATIONS = "key_operations";

    private SwipeRefreshLayout refresher;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_history);

        refresher = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresher.setOnRefreshListener(this);

        historyAdapter = new HistoryAdapter();
        if (state != null) {
            historyAdapter.setOperations(state.<OperationDisplayed>getParcelableArrayList(KEY_OPERATIONS));
        }

        RecyclerView historyView = (RecyclerView) findViewById(R.id.history_view);
        historyView.setLayoutManager(new LinearLayoutManager(this));
        historyView.setAdapter(historyAdapter);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_OPERATIONS, historyAdapter.getOperations());
    }


    @Override
    protected void onLoadFailed(Exception exception) {
        refresher.setRefreshing(false);
        if (exception instanceof ConnectException) return;
        loadHistoryFailed();
    }


    @Override
    protected void onOperationHistoryLoaded(OperationHistory operationHistory) {
        if (operationHistory.error != null) {
            loadHistoryFailed();
            refresher.setRefreshing(false);
            return;
        }

        historyAdapter.addOperations(operationHistory.operations);

        if (operationHistory.nextRecord != null) {
            loader.getOperationHistory(operationHistory.nextRecord);
        } else {
            refresher.setRefreshing(false);
        }
    }


    private void loadHistoryFailed() {
        Notifications.showToUser(R.string.load_history_failed);
    }


    @Override
    public void onRefresh() {
        loader.getOperationHistory();
    }
}
