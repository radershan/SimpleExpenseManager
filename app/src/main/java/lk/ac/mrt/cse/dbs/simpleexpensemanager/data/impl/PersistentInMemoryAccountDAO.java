package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;





/**
 * Created by Radershan on 19.11.2017.
 */

public class PersistentInMemoryAccountDAO implements AccountDAO  {
    SqlliteDatabaseHelper sqlliteDatabaseHelper;


    public PersistentInMemoryAccountDAO(Context context){

        sqlliteDatabaseHelper = new SqlliteDatabaseHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase sqlDB = sqlliteDatabaseHelper.getReadableDatabase();
        String [] column ={"accountNo"};
        Cursor cursor = sqlDB.query("Accounts",column,null,null,null,null,null);
        List accountNumbersList  = new ArrayList<>();

        while (cursor.moveToNext()){
            String itemId = cursor.getString(cursor.getColumnIndexOrThrow("accountNo"));
            accountNumbersList.add(itemId);

        }
        cursor.close();
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase sqlDB = sqlliteDatabaseHelper.getReadableDatabase();
        String [] column ={"accountNo","bankName", "accountHolderName","balance"};
        Cursor cursor = sqlDB.query("Accounts", column,
                null,null,null,null,null);

        List accountsList = new ArrayList<>();

        while(cursor.moveToNext()) {

            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow("accountNo"));
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow("bankName"));
            String accountHolderName = cursor.getString(cursor.getColumnIndexOrThrow("accountHolderName"));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));

            Account account = new Account(accountNo, bankName, accountHolderName, balance);

            accountsList.add(account);
        }

        cursor.close();
        return accountsList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqlDB = sqlliteDatabaseHelper.getReadableDatabase();
        String [] column = {"accountNo","bankName", "accountHolderName","balance"};
        String[] arg = {accountNo};
        Cursor cursor = sqlDB.query("Accounts", column,
                "accountNo = ?",arg,null,null,null);

        Account account = null;

        while(cursor.moveToNext()) {

            String accNo = cursor.getString(cursor.getColumnIndexOrThrow("accountNo"));
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow("bankName"));
            String accountHolderName = cursor.getString(cursor.getColumnIndexOrThrow("accountHolderName"));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));

            account = new Account(accNo, bankName, accountHolderName, balance);
        }

        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase sqlDB = sqlliteDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("accountNo",account.getAccountNo());
        values.put("bankName",account.getBankName());
        values.put("accountHolderName",account.getAccountHolderName());
        values.put("balance",account.getBalance());

        long newRowId = sqlDB.insert("Accounts", null, values);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqlDB = sqlliteDatabaseHelper.getWritableDatabase();

        String deleteAcc = "accountNo = ?";

        String[] deleteAccA = { accountNo };

        sqlDB.delete("Accounts", deleteAcc, deleteAccA );
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase sqlDB = sqlliteDatabaseHelper.getWritableDatabase();
        Account currentAcc = getAccount(accountNo);

        double value = 0;

        switch (expenseType) {
            case EXPENSE:
                value = currentAcc.getBalance() - amount;
                break;
            case INCOME:
                value = currentAcc.getBalance() + amount;
                break;
        }


        ContentValues values = new ContentValues();
        values.put("balance" , value );


        String selection = "accountNo = ?";
        String[] selectionArgs = { accountNo };

        int count = sqlDB.update(
                "Accounts",
                values,
                selection,
                selectionArgs);
    }
}
