package com.example.money;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IncomeDBHelper extends SQLiteOpenHelper {

    public static final String TABLE = "INCOME_TABLE";
    public static final String COL_ID = "INCOME_ID";
    public static final String COL_DESC = "INCOME_DESC";
    public static final String COL_AMNT = "INCOME_AMOUNT";
    public static final String COL_INC = "INCOME_INC";

    public IncomeDBHelper(@Nullable Context context) {
        super(context, "income.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Success", "Started making");
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DESC + " TEXT, "
                + COL_AMNT + " INT, "
                + COL_INC + " INT)";
        db.execSQL(createTableStatement);
        Log.d("Success", "Finished making");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }


    public List<Income> getAll(Boolean a) {
        List<Income> ret = new ArrayList<>();
        String query;
        if(a)
            query = "SELECT * FROM "+TABLE+" WHERE "+COL_INC+" = 1";
        else
            query = "SELECT * FROM "+TABLE+" WHERE "+COL_INC+" = -1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                int amount = c.getInt(2);
                int inc = c.getInt(3);
                Income i = new Income(id, desc, amount, inc);
                ret.add(i);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return ret;
    }

    public List<Income> getAll() {
        List<Income> ret = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                int amount = c.getInt(2);
                int inc = c.getInt(3);
                Income i = new Income(id, desc, amount, inc);
                ret.add(i);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return ret;
    }

    public boolean addOne(Income i) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();

        c.put(COL_DESC, i.getDesc());
        c.put(COL_AMNT, i.getAmountPerWeek());
        c.put(COL_INC, i.getInc());
        return db.insert(TABLE, null, c) != -1;
    }

    public boolean removeOne(Income i) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE + " WHERE " + COL_ID + " = " + i.getId();
        Cursor c = db.rawQuery(query, null);

        return c.moveToFirst();
    }

    public boolean editOne(Income i, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE +
                " SET " + COL_AMNT + " = " + i.getAmountPerWeek() + "" +
                ", " + COL_DESC + " = '" + i.getDesc() + "' " +
                " WHERE " + COL_ID + " = " + id;
        db.execSQL(query);
        return true;
    }
}
