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

    public static final String SPEND_TABLE = "SPENDING_TABLE";
    public static final String SPEND_COL_ID = "SPENDING_ID";
    public static final String SPEND_COL_DESC = "SPENDING_DESC";
    public static final String SPEND_COL_AMNT = "SPENDING_AMOUNT";
    public static final String SPEND_COL_NEC = "SPENDING_NECESSITY";
    public static final String SPEND_COL_DT = "SPENDING_DATETIME";

    public SpendingDBHelper(@Nullable Context context) {
        super(context, "spending.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + SPEND_TABLE + " ("
                + SPEND_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SPEND_COL_DESC + " TEXT, "
                + SPEND_COL_AMNT + " INT, "
                + SPEND_COL_NEC + " INTEGER, "
                + SPEND_COL_DT + " TEXT)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public List<Spending> getAllSpend() {
        List<Spending> ret = new ArrayList<>();
        String query;
        query = "SELECT * FROM "+SPEND_TABLE;
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

    public List<Spending> getAllSpend(String after, String before) {
        List<Spending> ret = new ArrayList<>();
        String query = "SELECT * FROM "+SPEND_TABLE+" WHERE "+SPEND_COL_DT+" >= '"+ after + "' AND "+ SPEND_COL_DT +" < '" + before+"' AND "+SPEND_COL_ID+"!= 1";

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

    public List<Spending> getAllSpend(boolean after, String dateTime) {
        List<Spending> ret = new ArrayList<>();
        if(dateTime==null)
            return ret;

        String query;
        if(after)
            query = "SELECT * FROM "+SPEND_TABLE+" WHERE "+SPEND_COL_DT+" >= '"+ dateTime + "' AND "+SPEND_COL_ID+"!= 1";
        else
            query = "SELECT * FROM "+SPEND_TABLE+" WHERE "+SPEND_COL_DT+" < '"+ dateTime + "' AND "+SPEND_COL_ID+"!= 1";

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
        return dateFormat.format(date).toString();
    }

    public boolean addOneSpend(Spending s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();

        c.put(SPEND_COL_DESC, s.getDesc());
        c.put(SPEND_COL_AMNT, s.getAmount());
        c.put(SPEND_COL_NEC, s.getNecessity());
        c.put(SPEND_COL_DT, getDateTime(s.getDateTime()));
        Log.d("Success", "Adding to db: "+getDateTime(s.getDateTime()));
        return db.insert(SPEND_TABLE, null, c) != -1;
    }

    public boolean removeOneSpend(Spending s) {
        if(s.getId()!=1) {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "DELETE FROM " + SPEND_TABLE + " WHERE " + SPEND_COL_ID + " = " + s.getId();
            Cursor c = db.rawQuery(query, null);
            return c.moveToFirst();
        }
        return false;
    }

    public boolean editOneSpend(Spending s, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query;
        if(id==1) {
            query = "UPDATE " + SPEND_TABLE +
                    " SET " + SPEND_COL_AMNT + " = " + s.getAmount() + "" +
                    ", " + SPEND_COL_DT + " = '" + getDateTime(s.getDateTime()) + "' " +
                    " WHERE " + SPEND_COL_ID + " = 1";
        }
        else {
            query = "UPDATE " + SPEND_TABLE +
                    " SET " + SPEND_COL_AMNT + " = " + s.getAmount() + "" +
                    ", " + SPEND_COL_DESC + " = '" + s.getDesc() + "' " +
                    ", " + SPEND_COL_NEC + " = " + s.getNecessity() + " " +
                    ", " + SPEND_COL_DT + " = '" + getDateTime(s.getDateTime()) + "' " +
                    " WHERE " + SPEND_COL_ID + " = " + id;
        }
        db.execSQL(query);
        return true;

    }

    public boolean setLastDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + SPEND_TABLE +
                " SET "+ SPEND_COL_DT + " = '" + getDateTime() + "' " +
                " WHERE " + SPEND_COL_ID + " = 1";
        db.execSQL(query);
        return true;
    }

    public Date getLastDate(){
        Date date = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "+SPEND_COL_DT+" FROM "+SPEND_TABLE+" WHERE "+SPEND_COL_ID+" = 1";
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
        String query = "SELECT "+SPEND_COL_AMNT+" FROM "+SPEND_TABLE+" WHERE "+SPEND_COL_ID+" = 1";
        Cursor c = db.rawQuery(query, null);
        if(c.moveToFirst()) {
            wA = c.getInt(0);
        }
        c.close();
        db.close();
        return wA;
    }


}
















