package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Radershan on 19.11.2017.
 */

public class SqlliteDatabaseHelper extends SQLiteOpenHelper {

    public SqlliteDatabaseHelper(Context context) {
        super(context, "150487T.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE Accounts (" +
                "accountNo TEXT PRIMARY KEY, " +
                "bankName TEXT, " +
                "accountHolderName TEXT, " +
                "balance REAL)");

        sqLiteDatabase.execSQL("CREATE TABLE Transactions (" +
                "date TEXT, " +
                "accountNo TEXT, " +
                "expenceType TEXT, " +
                "amount REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
