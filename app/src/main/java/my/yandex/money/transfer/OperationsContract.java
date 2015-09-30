package my.yandex.money.transfer;

import android.provider.BaseColumns;

public class OperationsContract {
    public static final String SQL_CREATE_TABLE =
        "CREATE TABLE " + Operations.TABLE_NAME + " (" +
            Operations._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Operations.OPERATION_ID + " TEXT UNIQUE," +
            Operations.TITLE + " TEXT NOT NULL," +
            Operations.AMOUNT + " TEXT NOT NULL," +
            Operations.IS_INCOMING + " INTEGER NOT NULL," +
            Operations.DATE_TIME + " INTEGER NOT NULL)";

    public static final String SQL_DROP_TABLE =
        "DROP TABLE IF EXISTS " + Operations.TABLE_NAME;

    public static abstract class Operations implements BaseColumns {
        public static final String TABLE_NAME = "operations";

        public static final String OPERATION_ID = "operation_id";
        public static final String TITLE = "title";
        public static final String AMOUNT = "amount";
        public static final String IS_INCOMING = "is_incoming";
        public static final String DATE_TIME = "date_time";

        public static final String[] PROJECTION = {
            OPERATION_ID, TITLE, AMOUNT, IS_INCOMING
        };
    }
}
