package com.example.money;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDBHelper extends SQLiteOpenHelper {

    public static final String TABLE = "EXPENSE_TABLE";
    public static final String COL_ID = "EXPENSE_ID";
    public static final String COL_DESC = "EXPENSE_DESC";
    public static final String COL_AMNT = "EXPENSE_AMOUNT";

    public ExpenseDBHelper(@Nullable Context context) {
        super(context, "Expense.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DESC + " TEXT, "
                + COL_AMNT + " INT)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


    public List<Expense> getAll() {
        List<Expense> ret = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                int amount = c.getInt(2);
                Expense i = new Expense(id, desc, amount);
                ret.add(i);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return ret;
    }

    public boolean addOne(Expense i) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();

        c.put(COL_DESC, i.getDesc());
        c.put(COL_AMNT, i.getAmountPerWeek());

        return db.insert(TABLE, null, c) == 1 ? true:false;
    }

    public boolean removeOne(Expense i) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE + " WHERE " + COL_ID + " = " + i.getId();
        Cursor c = db.rawQuery(query, null);

        return c.moveToFirst();
    }

    public boolean editOne(Expense i, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE +
                " SET " + COL_AMNT + " = " + i.getAmountPerWeek() + "" +
                ", " + COL_DESC + " = '" + i.getDesc() + "' " +
                " WHERE " + COL_ID + " = " + id;
        db.execSQL(query);
        return true;
    }
}
