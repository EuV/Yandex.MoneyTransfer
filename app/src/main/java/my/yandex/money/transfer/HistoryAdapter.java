package my.yandex.money.transfer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.OperationViewHolder> {
    private ArrayList<OperationSub> operations = new ArrayList<>();

    public void setOperations(ArrayList<OperationSub> operations) {
        this.operations = operations;
        notifyDataSetChanged();
    }


    public ArrayList<OperationSub> getOperations() {
        return operations;
    }


    @Override
    public OperationViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.operation_view, parent, false);
        return new OperationViewHolder(v);
    }


    @Override
    public void onBindViewHolder(OperationViewHolder holder, int i) {
        OperationSub operation = operations.get(i);
        holder.title.setText(operation.title);
        holder.amount.setText(operation.amount);
        if (operation.isIncoming) {
            holder.direction.setImageResource(R.drawable.ic_keyboard_tab_black_24dp);
        } else {
            holder.direction.setImageResource(R.drawable.ic_keyboard_tab_black_24dp_left);
        }
    }


    @Override
    public int getItemCount() {
        return operations.size();
    }


    class OperationViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView direction;
        TextView amount;

        public OperationViewHolder(View operation) {
            super(operation);
            title = (TextView) operation.findViewById(R.id.title);
            amount = (TextView) operation.findViewById(R.id.amount);
            direction = (ImageView) operation.findViewById(R.id.direction);
        }
    }
}
