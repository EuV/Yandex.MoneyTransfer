package my.yandex.money.transfer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * For ease of handling, stores only displayed Operation details
 */
public class OperationSub implements Parcelable {
    public final String title;
    public final String amount;
    public final String operationId;
    public final boolean isIncoming;

    public OperationSub(String title, String amount, String operationId, boolean isIncoming) {
        this.title = title;
        this.amount = amount;
        this.operationId = operationId;
        this.isIncoming = isIncoming;
    }

    private OperationSub(Parcel src) {
        this(src.readString(), src.readString(), src.readString(), (boolean) src.readValue(null));
    }


    @Override
    public int hashCode() {
        return operationId.hashCode();
    }


    @Override
    public boolean equals(Object o) {
        return (o instanceof OperationSub)
            && operationId.equals(((OperationSub) o).operationId);
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


    public static final Creator<OperationSub> CREATOR = new Creator<OperationSub>() {
        @Override
        public OperationSub createFromParcel(Parcel src) {
            return new OperationSub(src);
        }

        @Override
        public OperationSub[] newArray(int size) {
            return new OperationSub[size];
        }
    };
}
