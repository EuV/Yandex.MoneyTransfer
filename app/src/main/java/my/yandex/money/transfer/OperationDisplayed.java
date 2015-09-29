package my.yandex.money.transfer;

import android.os.Parcel;
import android.os.Parcelable;
import com.yandex.money.api.model.Operation;

import static com.yandex.money.api.model.Operation.Direction.INCOMING;

/**
 * For ease of handling, stores only displayed Operation details
 */
public class OperationDisplayed implements Parcelable {
    public final String title;
    public final String amount;
    public final String operationId;
    public final boolean isIncoming;


    public OperationDisplayed(Operation operation) {
        title = operation.title;
        amount = operation.amount.toString();
        operationId = operation.operationId;
        isIncoming = (operation.direction == INCOMING);
    }


    private OperationDisplayed(Parcel src) {
        title = src.readString();
        amount = src.readString();
        operationId = src.readString();
        isIncoming = (boolean) src.readValue(null);
    }


    @Override
    public int hashCode() {
        return operationId.hashCode();
    }


    @Override
    public boolean equals(Object o) {
        return (o instanceof OperationDisplayed)
            && operationId.equals(((OperationDisplayed) o).operationId);
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(amount);
        dest.writeString(operationId);
        dest.writeValue(isIncoming);
    }


    public static final Creator<OperationDisplayed> CREATOR = new Creator<OperationDisplayed>() {
        @Override
        public OperationDisplayed createFromParcel(Parcel src) {
            return new OperationDisplayed(src);
        }

        @Override
        public OperationDisplayed[] newArray(int size) {
            return new OperationDisplayed[size];
        }
    };
}
