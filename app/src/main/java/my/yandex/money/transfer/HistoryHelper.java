package my.yandex.money.transfer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import com.yandex.money.api.model.Operation;
import java.util.ArrayList;
import java.util.List;
import my.yandex.money.transfer.OperationsContract.Operations;
import my.yandex.money.transfer.utils.Preferences;

import static com.yandex.money.api.model.Operation.Direction.INCOMING;

public class HistoryHelper {
    private static Context context;

    public static void init(Context c) {
        context = c;
    }

    public static void save(OnDBOperationPerformed listener, List<Operation> operations) {
        new SaveTask(listener, operations).execute();
    }


    public static void load(OnDBOperationPerformed listener) {
        new LoadTask(listener).execute();
    }


    private static class SaveTask extends AsyncTask<Void, Void, Boolean> {
        private final OnDBOperationPerformed listener;
        private final List<Operation> operations;

        public SaveTask(OnDBOperationPerformed listener, List<Operation> operations) {
            this.listener = listener;
            this.operations = operations;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String login = Preferences.getLogin();
            SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
            for (Operation operation : operations) {
                ContentValues val = new ContentValues();
                val.put(Operations.LOGIN, login);
                val.put(Operations.TITLE, operation.title);
                val.put(Operations.AMOUNT, operation.amount.toString());
                val.put(Operations.OPERATION_ID, operation.operationId);
                val.put(Operations.IS_INCOMING, operation.direction == INCOMING);
                val.put(Operations.DATE_TIME, operation.datetime.getMillis());

                long id = db.insertWithOnConflict(Operations.TABLE_NAME, null, val, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean furtherUpdateNeeded) {
            if (listener != null) {
                listener.onOperationsStoredInDB(furtherUpdateNeeded);
            }
        }
    }


    private static class LoadTask extends AsyncTask<Void, Void, ArrayList<OperationSub>> {
        private final OnDBOperationPerformed listener;

        public LoadTask(OnDBOperationPerformed listener) {
            this.listener = listener;
        }

        @Override
        protected ArrayList<OperationSub> doInBackground(Void... params) {
            SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
            ArrayList<OperationSub> operations = new ArrayList<>();
            String selection = Operations.LOGIN + " = '" + Preferences.getLogin() + "'";
            Cursor cursor = db.query(Operations.TABLE_NAME, Operations.PROJECTION, selection, null, null, null, Operations.DATE_TIME);
            try {
                if (!cursor.moveToLast()) return operations;
                do {
                    String title = getString(cursor, Operations.TITLE);
                    String amount = getString(cursor, Operations.AMOUNT);
                    String operationId = getString(cursor, Operations.OPERATION_ID);
                    boolean isIncoming = getBoolean(cursor, Operations.IS_INCOMING);
                    operations.add(new OperationSub(title, amount, operationId, isIncoming));
                } while (cursor.moveToPrevious());
            } finally {
                cursor.close();
            }
            return operations;
        }

        @Override
        protected void onPostExecute(ArrayList<OperationSub> operations) {
            if (listener != null) {
                listener.onOperationsLoadedFromDB(operations);
            }
        }

        private boolean getBoolean(Cursor cursor, String column) {
            return (cursor.getInt(cursor.getColumnIndexOrThrow(column)) == 1);
        }

        private String getString(Cursor cursor, String column) {
            return cursor.getString(cursor.getColumnIndexOrThrow(column));
        }
    }


    private static class DBHelper extends SQLiteOpenHelper {
        static final int DATABASE_VERSION = 2;
        static final String DATABASE_NAME = "Operations.db";

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(OperationsContract.SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(OperationsContract.SQL_DROP_TABLE);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }


    public interface OnDBOperationPerformed {
        void onOperationsLoadedFromDB(ArrayList<OperationSub> operations);
        void onOperationsStoredInDB(boolean furtherUpdateNeeded);
    }
}
