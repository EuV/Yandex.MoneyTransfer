package my.yandex.money.transfer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.yandex.money.api.model.Operation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.OperationViewHolder> {
    private List<Operation> operations = new ArrayList<>();
    private Set<String> savedOperationIds = new HashSet<>();


    public void addOperations(List<Operation> newOperations) {
        for (Operation operation : newOperations) {
            if (!savedOperationIds.contains(operation.operationId)) {
                savedOperationIds.add(operation.operationId);
                operations.add(operation);
                notifyItemRangeInserted(operations.size(), 1);
            }
        }
    }


    @Override
    public OperationViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.operation_view, parent, false);
        return new OperationViewHolder(v);
    }


    @Override
    public void onBindViewHolder(OperationViewHolder operationViewHolder, int i) {
        operationViewHolder.name.setText(operations.get(i).title);
    }


    @Override
    public int getItemCount() {
        return operations.size();
    }


    class OperationViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public OperationViewHolder(View operation) {
            super(operation);
            name = (TextView) operation.findViewById(R.id.name);
        }
    }
}
