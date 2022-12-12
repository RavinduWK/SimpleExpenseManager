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
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Type;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.BALANCE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.BANK;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.HOLDER_NAME;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.ACCOUNTS_TABLE;


/**
 * This is an a Persistent implementation of the AccountDAO interface.
 * A SQLite database is used to store the account details permanently in backend and to retrieve when needed.
 */
public class PersistentAccountDAO implements AccountDAO {
    private final SQLiteHelper helper;
    private SQLiteDatabase database;

    public PersistentAccountDAO(Context context) {
        helper = new SQLiteHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        database = helper.getReadableDatabase();

        String[] projection = {
                ACCOUNT_NO
        };

        Cursor cursor = database.query(
                ACCOUNTS_TABLE,    // The table to query
                projection,        // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,              // The values for the WHERE clause
                null,              // don't group the rows
                null,              // don't filter by row groups
                null               // The sort order
        );
        List<String> accountNumbers = new ArrayList<String>();

        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow(ACCOUNT_NO));
            accountNumbers.add(accountNumber);
        }
        cursor.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<>();

        database = helper.getReadableDatabase();

        String[] projection = {
                ACCOUNT_NO,
                BANK,
                HOLDER_NAME,
                BALANCE
        };

        Cursor cursor = database.query(
                ACCOUNTS_TABLE,    // The table to query
                projection,        // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,              // The values for the WHERE clause
                null,              // don't group the rows
                null,              // don't filter by row groups
                null               // The sort order
        );

        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String bankName = cursor.getString(cursor.getColumnIndex(BANK));
            String accountHolderName = cursor.getString(cursor.getColumnIndex(HOLDER_NAME));
            double balance = cursor.getDouble(cursor.getColumnIndex(BALANCE));
            Account account = new Account(accountNumber,bankName,accountHolderName,balance);

            accounts.add(account);
        }
        cursor.close();

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        database = helper.getReadableDatabase();
        String[] projection = {
                ACCOUNT_NO,
                BANK,
                HOLDER_NAME,
                BALANCE
        };

        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor cursor = database.query(
                ACCOUNTS_TABLE,     // The table to query
                projection,         // The array of columns to return (pass null to get all)
                selection,          // The columns for the WHERE clause
                selectionArgs,      // The values for the WHERE clause
                null,               // don't group the rows
                null,               // don't filter by row groups
                null                // The sort order
        );

        if (cursor == null){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else {
            cursor.moveToFirst();

            Account account = new Account(accountNo, cursor.getString(cursor.getColumnIndex(BANK)), cursor.getString(cursor.getColumnIndex(HOLDER_NAME)), cursor.getDouble(cursor.getColumnIndex(BALANCE)));
            return account;
        }
    }

    @Override
    public void addAccount(Account account) {
        database = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACCOUNT_NO, account.getAccountNo());
        values.put(BANK, account.getBankName());
        values.put(HOLDER_NAME, account.getAccountHolderName());
        values.put(BALANCE,account.getBalance());

        // insert row
        database.insert(ACCOUNTS_TABLE, null, values);
        database.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        database = helper.getWritableDatabase();
        database.delete(ACCOUNTS_TABLE, ACCOUNT_NO + " = ?",
                new String[] { accountNo });
        database.close();
    }

    @Override
    public void updateBalance(String accountNo, Type expenseType, double amount) throws InvalidAccountException {
        database = helper.getWritableDatabase();
        String[] projection = {
                BALANCE
        };

        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor cursor = database.query(
                ACCOUNTS_TABLE,    // The table to query
                projection,        // The array of columns to return (pass null to get all)
                selection,         // The columns for the WHERE clause
                selectionArgs,     // The values for the WHERE clause
                null,              // don't group the rows
                null,              // don't filter by row groups
                null               // The sort order
        );

        double balance;
        if(cursor.moveToFirst())
            balance = cursor.getDouble(0);
        else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        ContentValues values = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                values.put(BALANCE, balance - amount);
                break;
            case INCOME:
                values.put(BALANCE, balance + amount);
                break;
        }

        // updating row
        database.update(ACCOUNTS_TABLE, values, ACCOUNT_NO + " = ?",
                new String[] { accountNo });

        cursor.close();
        database.close();

    }
}

