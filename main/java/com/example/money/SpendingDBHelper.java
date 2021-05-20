package com.example.money;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpendingDBHelper extends SQLiteOpenHelper{

    public static final String TABLE = "SPENDING_TABLE";
    public static final String COL_ID = "SPENDING_ID";
    public static final String COL_DESC = "SPENDING_DESC";
    public static final String COL_AMNT = "SPENDING_AMOUNT";
    public static final String COL_NEC = "SPENDING_NECESSITY";
    public static final String COL_DT = "SPENDING_DATETIME";

    public SpendingDBHelper(@Nullable Context context) {
        super(context, "spending.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Success", "Started making");
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DESC + " TEXT, "
                + COL_AMNT + " INT, "
                + COL_NEC + " INTEGER, "
                + COL_DT + " TEXT)";
        db.execSQL(createTableStatement);
        Log.d("Success", "Finished making");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public List<Spending> getAll() {
        List<Spending> ret = new ArrayList<>();
        String query;
        query = "SELECT * FROM "+TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                int amount = c.getInt(2);
                int nec = c.getInt(3);
                String dt = c.getString(4);
                Spending s = new Spending(id, desc, amount, nec==1, dt);
                ret.add(s);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return ret;
    }

    public List<Spending> getAll(String after, String before) {
        List<Spending> ret = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE+" WHERE "+COL_DT+" >= '"+ after + "' AND "+ COL_DT +" < '" + before+"' AND "+COL_ID+"!= 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                int amount = c.getInt(2);
                int nec = c.getInt(3);
                String dt = c.getString(4);
                Spending s = new Spending(id, desc, amount, nec==1, dt);
                ret.add(s);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return ret;
    }

    public List<Spending> getAll(boolean after, String dateTime) {
        List<Spending> ret = new ArrayList<>();
        if(dateTime==null)
            return ret;

        String query;
        if(after)
            query = "SELECT * FROM "+TABLE+" WHERE "+COL_DT+" >= '"+ dateTime + "' AND "+COL_ID+"!= 1";
        else
            query = "SELECT * FROM "+TABLE+" WHERE "+COL_DT+" < '"+ dateTime + "' AND "+COL_ID+"!= 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                int amount = c.getInt(2);
                int nec = c.getInt(3);
                String dt = c.getString(4);
                Spending s = new Spending(id, desc, amount, nec==1, dt);
                ret.add(s);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return ret;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    public boolean addOne(Spending s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();

        c.put(COL_DESC, s.getDesc());
        c.put(COL_AMNT, s.getAmount());
        c.put(COL_NEC, s.getNecessity());
        c.put(COL_DT, getDateTime(s.getDateTime()));
        return db.insert(TABLE, null, c) != -1;
    }

    public boolean removeOne(Spending s) {
        if(s.getId()!=1) {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "DELETE FROM " + TABLE + " WHERE " + COL_ID + " = " + s.getId();
            Cursor c = db.rawQuery(query, null);
            return c.moveToFirst();
        }
        return false;
    }

    public boolean editOne(Spending s, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query;
        if(id==1) {
            query = "UPDATE " + TABLE +
                    " SET " + COL_AMNT + " = " + s.getAmount() + "" +
                    ", " + COL_DT + " = '" + s.getDateTime() + "' " +
                    " WHERE " + COL_ID + " = 1";
        }
        else {
            query = "UPDATE " + TABLE +
                    " SET " + COL_AMNT + " = " + s.getAmount() + "" +
                    ", " + COL_DESC + " = '" + s.getDesc() + "' " +
                    ", " + COL_NEC + " = " + s.getNecessity() + " " +
                    ", " + COL_DT + " = '" + s.getDateTime() + "' " +
                    " WHERE " + COL_ID + " = " + id;
        }
        db.execSQL(query);
        return true;

    }

    public boolean setLastDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE +
                " SET "+ COL_DT + " = '" + getDateTime() + "' " +
                " WHERE " + COL_ID + " = 1";
        db.execSQL(query);
        return true;
    }

    public Date getLastDate(){
        Date date = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "+COL_DT+" FROM "+TABLE+" WHERE "+COL_ID+" = 1";
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(c.getString(0));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        c.close();
        db.close();
        return date;
    }

    public int getWeeklyAllowance() {
        int wA = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "+COL_AMNT+" FROM "+TABLE+" WHERE "+COL_ID+" = 1";
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()) {
            wA = c.getInt(0);
        }
        c.close();
        db.close();
        return wA;
    }


}
















