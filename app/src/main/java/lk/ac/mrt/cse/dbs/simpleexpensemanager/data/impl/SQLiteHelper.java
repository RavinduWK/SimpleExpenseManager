package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "200708G.sqlite";
    private static final int VERSION = 1;

    //TABLES
    public static final String ACCOUNTS_TABLE = "accounts";
    public static final String TRANSACTIONS_TABLE = "transactions";

    //COMMON COLUMNS
    public static final String ACCOUNT_NO = "accountNo";

    //ACCOUNT TABLE - COLUMNS
    public static final String HOLDER_NAME = "accountHolderName";
    public static final String BANK = "bankName";
    public static final String BALANCE = "balance";

    //TRANSACTION TABLE - COLUMNS
    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String TYPE = "type";
    public static final String AMOUNT = "amount";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    // create tables for the database
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ACCOUNTS_TABLE + "(" +
                ACCOUNT_NO + " TEXT PRIMARY KEY, " +
                BANK + " TEXT NOT NULL, " +
                HOLDER_NAME + " TEXT NOT NULL, " +
                BALANCE + " REAL NOT NULL)");

        db.execSQL("CREATE TABLE " + TRANSACTIONS_TABLE + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE + " TEXT NOT NULL, " +
                TYPE + " TEXT NOT NULL, " +
                AMOUNT + " REAL NOT NULL, " +
                ACCOUNT_NO + " TEXT," +
                "FOREIGN KEY (" + ACCOUNT_NO + ") REFERENCES " + ACCOUNTS_TABLE + "(" + ACCOUNT_NO + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTIONS_TABLE);

        // create new tables
        onCreate(db);
    }

}
