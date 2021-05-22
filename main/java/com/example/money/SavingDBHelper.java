package com.example.money;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SavingDBHelper extends SQLiteOpenHelper {

    public static final String SAVE_TABLE = "SAVING_TABLE";
    public static final String SAVE_COL_ID = "SAVING_ID";
    public static final String SAVE_COL_DESC = "SAVING_DESC";
    public static final String SAVE_COL_LIMT = "SAVING_LIMT";
    public static final String SAVE_COL_STOR = "SAVING_STOR";
    public static final String SAVE_COL_WEEK = "SAVING_WEEK";
    public static final String SAVE_COL_PERC = "SAVING_PERC";
    public static final String SAVE_COL_REMV = "SAVING_REMV";

    public SavingDBHelper(@Nullable Context context) {
        super(context, "saving.db", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Success", "Started making saving");
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + SAVE_TABLE + " ("
                + SAVE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SAVE_COL_DESC + " TEXT, "
                + SAVE_COL_LIMT + " INTEGER, "
                + SAVE_COL_STOR + " INTEGER, "
                + SAVE_COL_WEEK + " INTEGER, "
                + SAVE_COL_PERC + " REAL, "
                + SAVE_COL_REMV + " INTEGER )";
        db.execSQL(createTableStatement);
        Log.d("Success", "Finished making saving");
    }


    public boolean addOneSave(Saving s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();

        c.put(SAVE_COL_DESC, s.getDesc());
        c.put(SAVE_COL_LIMT, s.getLimitStored());
        c.put(SAVE_COL_STOR, s.getAmountStored());
        c.put(SAVE_COL_WEEK, s.getAmountPerWeek());
        c.put(SAVE_COL_PERC, s.getPercent());
        c.put(SAVE_COL_REMV, s.getCanTakeFrom());
        return db.insert(SAVE_TABLE, null, c) != -1;
    }


    public boolean editOneSave(Saving s, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + SAVE_TABLE +
                " SET " + SAVE_COL_LIMT + " = " + s.getLimitStored() +
                ", " + SAVE_COL_DESC + " = '" + s.getDesc() + "' " +
                ", " + SAVE_COL_WEEK + " = " + s.getAmountPerWeek() +
                ", " + SAVE_COL_PERC + " = " + s.getPercent() +
                ", " + SAVE_COL_REMV + " = " + s.getCanTakeFrom() +
                " WHERE " + SAVE_COL_ID + " = " + id;
        Log.d("Success", "working on it");
        db.execSQL(query);
        return true;
    }

    public boolean removeOneSave(Saving s) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + SAVE_TABLE + " WHERE " + SAVE_COL_ID + " = " + s.getId();
        Cursor c = db.rawQuery(query, null);

        return c.moveToFirst();
    }

    public List<Saving> getAllNonFinishedSave() {
        List<Saving> ret = new ArrayList<>();
        String query = "SELECT * FROM "+SAVE_TABLE+" WHERE "+SAVE_COL_STOR+" < "+SAVE_COL_LIMT;

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

    public List<Saving> getAllLongTermSave() {
        List<Saving> ret = new ArrayList<>();
        long max = Long.MAX_VALUE;
        String query = "SELECT * FROM "+SAVE_TABLE+" WHERE "+SAVE_COL_LIMT+" = "+max;

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

    public List<Saving> getAllShortTermSave() {
        List<Saving> ret = new ArrayList<>();
        long max = Long.MAX_VALUE;
        String query = "SELECT * FROM "+SAVE_TABLE+" WHERE "+SAVE_COL_LIMT+" != "+max;

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

    public void updatePercentAmountsSave(int remaining) {
        List<Saving> ret = new ArrayList<>();
        long max = Long.MAX_VALUE;
        String query = "SELECT * FROM "+SAVE_TABLE+" WHERE "+SAVE_COL_LIMT+" = "+max;

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
                query = "UPDATE " + SAVE_TABLE +
                        " SET " + SAVE_COL_WEEK + " = " + s.getAmountPerWeek() +
                        " WHERE " + SAVE_COL_ID + " = " + s.getId();
                db.execSQL(query);
            }
        }
    }

    public List<Saving> getAllRemoveableSave() {
        List<Saving> ret = new ArrayList<>();
        String query = "SELECT * FROM "+SAVE_TABLE+" WHERE "+SAVE_COL_REMV+" = 1";

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
        String query = "UPDATE " + SAVE_TABLE +
                " SET " + SAVE_COL_STOR + " = " + (s.getAmountStored()-amount) +
                " WHERE " + SAVE_COL_ID + " = " + s.getId();
        db.execSQL(query);
        return true;
    }

    public int updateSavingAmounts(Context c) {
        int left = Databases.getWeeklyAfterExpenses();
        Log.d("Success", "Weekly after expenses "+left);
        List<Saving> addAmount = getAllNonFinishedSave();
        String query;
        updatePercentAmountsSave(left);

        SQLiteDatabase db = this.getWritableDatabase();
        for(Saving s: addAmount) {
            if(s.getLimitStored()<s.getAmountStored()+s.getAmountPerWeek()) {
                query = "UPDATE " + SAVE_TABLE +
                        " SET " + SAVE_COL_STOR + " = " + s.getLimitStored() +
                        ", " + SAVE_COL_REMV + " = 1" +
                        " WHERE " + SAVE_COL_ID + " = " + s.getId();
                Toast.makeText(c, "Congratulations, you've reached your goal for "+s.getDesc(), Toast.LENGTH_LONG).show();
                Toast.makeText(c, "You can now spend the "+s.getLimitStored()+" that you've saved up.", Toast.LENGTH_LONG).show();
                left -= s.getLimitStored()-s.getAmountStored();
            }
            else {
                query = "UPDATE " + SAVE_TABLE +
                        " SET " + SAVE_COL_STOR + " = " + (s.getAmountStored() + s.getAmountPerWeek()) +
                        " WHERE " + SAVE_COL_ID + " = " + s.getId();
                left -= s.getAmountPerWeek();
            }
            db.execSQL(query);
        }
        Log.d("Success", "Weekly after savings "+left);
        return left;
    }
}
