package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.Date;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Radershan on 19.11.2017.
 */

public class PersistentInMemoryTransactionDAO implements TransactionDAO {

    SqlliteDatabaseHelper sqlliteDatabaseHelper;

    public PersistentInMemoryTransactionDAO(Context context){
        sqlliteDatabaseHelper = new SqlliteDatabaseHelper(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase sqlDB = sqlliteDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("accountNo",accountNo);
        values.put("date", date.toString());
        values.put("expenceType",expenseType.toString());
        values.put("amount",amount);

// Insert the new row, returning the primary key value of the new row
        long newRowId = sqlDB.insert("Transactions", null, values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs(){

        SQLiteDatabase sqlDB = sqlliteDatabaseHelper.getReadableDatabase();
        String [] column = {"date","accountNo", "expenceType","amount"};
        Cursor cursor = sqlDB.query("Transactions", column,
                null,null,null,null,null);

        List transactions = new ArrayList<>();

        while(cursor.moveToNext()) {

            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow("accountNo"));
            String expenceType = cursor.getString(cursor.getColumnIndexOrThrow("expenceType"));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

            ExpenseType x = null;

            if(expenceType.equals("EXPENSE")){
                x = ExpenseType.EXPENSE;
            }
            else{
                x = ExpenseType.INCOME;
            }

            Date date1= null;
            try {
                date1 = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Transaction transaction = new Transaction(date1, accountNo, x, amount);
            transactions.add(transaction);

        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit){
        SQLiteDatabase sqlDB = sqlliteDatabaseHelper.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(sqlDB, "Transactions");

        if(limit<=cnt){
            return getAllTransactionLogs();
        }
        else{
            String [] columnX = {"date","accountNo", "expenceType","amount"};
            Cursor cursor = sqlDB.query("Transactions", columnX,
                    null,null,null,null,null);

            List transactions = new ArrayList<>();
            int count = 0;

            while(cursor.moveToNext() && count< limit) {

                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String accountNo = cursor.getString(cursor.getColumnIndexOrThrow("accountNo"));
                String expenceType = cursor.getString(cursor.getColumnIndexOrThrow("expenceType"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

                ExpenseType x = null;

                if(expenceType.equals("EXPENSE")){
                    x = ExpenseType.EXPENSE;
                }
                else{
                    x = ExpenseType.INCOME;
                }

                Date date1= null;
                try {
                    date1 = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(date);

                } catch (ParseException e) {

                    e.printStackTrace();
                }

                Transaction transaction = new Transaction(date1, accountNo, x, amount);
                transactions.add(transaction);
                count++;
            }

            cursor.close();
            return transactions;
        }
    }
}
