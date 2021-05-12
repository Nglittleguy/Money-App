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

public class SavingDBHelper extends SQLiteOpenHelper {

    public static final String TABLE = "SAVING_TABLE";
    public static final String COL_ID = "SAVING_ID";
    public static final String COL_DESC = "SAVING_DESC";
    public static final String COL_LIMT = "SAVING_LIMT";
    public static final String COL_STOR = "SAVING_STOR";
    public static final String COL_WEEK = "SAVING_WEEK";
    public static final String COL_PERC = "SAVING_PERC";
    public static final String COL_REMV = "SAVING_REMV";

    public SavingDBHelper(@Nullable Context context) {
        super(context, "saving.db", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Success", "Started making saving");
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DESC + " TEXT, "
                + COL_LIMT + " INTEGER, "
                + COL_STOR + " INTEGER, "
                + COL_WEEK + " INTEGER, "
                + COL_PERC + " REAL, "
                + COL_REMV + " INTEGER )";
        db.execSQL(createTableStatement);
        Log.d("Success", "Finished making saving");
    }


    public boolean addOne(Saving s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();

        c.put(COL_DESC, s.getDesc());
        c.put(COL_LIMT, s.getLimitStored());
        c.put(COL_STOR, s.getAmountStored());
        c.put(COL_WEEK, s.getAmountPerWeek());
        c.put(COL_PERC, s.getPercent());
        c.put(COL_REMV, s.getCanTakeFrom());
        return db.insert(TABLE, null, c) != -1;
    }


    public boolean editOne(Saving s, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE +
                " SET " + COL_LIMT + " = " + s.getLimitStored() +
                ", " + COL_DESC + " = '" + s.getDesc() + "' " +
                ", " + COL_WEEK + " = " + s.getAmountPerWeek() +
                ", " + COL_PERC + " = " + s.getPercent() +
                ", " + COL_REMV + " = " + s.getCanTakeFrom() +
                " WHERE " + COL_ID + " = " + id;
        Log.d("Success", "working on it");
        db.execSQL(query);
        return true;
    }

    public boolean removeOne(Saving s) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE + " WHERE " + COL_ID + " = " + s.getId();
        Cursor c = db.rawQuery(query, null);

        return c.moveToFirst();
    }

    public List<Saving> getAllNonFinished() {
        List<Saving> ret = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE+" WHERE "+COL_STOR+" < "+COL_LIMT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                long limit = c.getLong(2);
                long stored = c.getLong(3);
                int week = c.getInt(4);
                double percent = c.getDouble(5);
                int removeable = c.getInt(6);
                Saving s = new Saving(id, desc, limit, stored, week, percent, removeable);
                ret.add(s);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return ret;
    }

    public List<Saving> getAllLongTerm() {
        List<Saving> ret = new ArrayList<>();
        long max = Long.MAX_VALUE;
        String query = "SELECT * FROM "+TABLE+" WHERE "+COL_LIMT+" = "+max;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                long limit = c.getLong(2);
                long stored = c.getLong(3);
                int week = c.getInt(4);
                double percent = c.getDouble(5);
                int removeable = c.getInt(6);
                Saving s = new Saving(id, desc, limit, stored, week, percent, removeable);
                ret.add(s);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return ret;
    }

    public List<Saving> getAllShortTerm() {
        List<Saving> ret = new ArrayList<>();
        long max = Long.MAX_VALUE;
        String query = "SELECT * FROM "+TABLE+" WHERE "+COL_LIMT+" != "+max;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                long limit = c.getLong(2);
                long stored = c.getLong(3);
                int week = c.getInt(4);
                double percent = c.getDouble(5);
                int removeable = c.getInt(6);
                Saving s = new Saving(id, desc, limit, stored, week, percent, removeable);
                ret.add(s);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return ret;
    }

    public void updatePercentAmounts(int remaining) {
        List<Saving> ret = new ArrayList<>();
        long max = Long.MAX_VALUE;
        String query = "SELECT * FROM "+TABLE+" WHERE "+COL_LIMT+" = "+max;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                long limit = c.getLong(2);
                long stored = c.getLong(3);
                int week = c.getInt(4);
                double percent = c.getDouble(5);
                int removeable = c.getInt(6);
                Saving s = new Saving(id, desc, limit, stored, week, percent, removeable);
                ret.add(s);
            }while(c.moveToNext());
        }
        c.close();
        db.close();

        db = this.getWritableDatabase();
        for(Saving s: ret) {
            if(s.getPercent()!=0) {
                s.setAmountPerWeek((int) s.getPercent()*remaining/100);
                query = "UPDATE " + TABLE +
                        " SET " + COL_WEEK + " = " + s.getAmountPerWeek() +
                        " WHERE " + COL_ID + " = " + s.getId();
                db.execSQL(query);
            }
        }
    }

    public List<Saving> getAllRemoveable() {
        List<Saving> ret = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE+" WHERE "+COL_REMV+" = 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                long limit = c.getLong(2);
                long stored = c.getLong(3);
                int week = c.getInt(4);
                double percent = c.getDouble(5);
                int removeable = c.getInt(6);
                Saving s = new Saving(id, desc, limit, stored, week, percent, removeable);
                ret.add(s);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return ret;
    }

    public boolean takeFromSavings(Saving s, int amount) {
        if(s.getCanTakeFrom()!=1)
            return false;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE +
                " SET " + COL_STOR + " = " + (s.getAmountStored()-amount) +
                " WHERE " + COL_ID + " = " + s.getId();
        db.execSQL(query);
        return true;
    }
}
