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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String INCOME_TABLE = "INCOME_TABLE";
    public static final String INCOME_COL_ID = "INCOME_ID";
    public static final String INCOME_COL_DESC = "INCOME_DESC";
    public static final String INCOME_COL_AMNT = "INCOME_AMOUNT";
    public static final String INCOME_COL_INC = "INCOME_INC";

    public static final String SAVE_TABLE = "SAVING_TABLE";
    public static final String SAVE_COL_ID = "SAVING_ID";
    public static final String SAVE_COL_DESC = "SAVING_DESC";
    public static final String SAVE_COL_LIMT = "SAVING_LIMT";
    public static final String SAVE_COL_STOR = "SAVING_STOR";
    public static final String SAVE_COL_WEEK = "SAVING_WEEK";
    public static final String SAVE_COL_PERC = "SAVING_PERC";
    public static final String SAVE_COL_REMV = "SAVING_REMV";

    public static final String SPEND_TABLE = "SPENDING_TABLE";
    public static final String SPEND_COL_ID = "SPENDING_ID";
    public static final String SPEND_COL_DESC = "SPENDING_DESC";
    public static final String SPEND_COL_AMNT = "SPENDING_AMOUNT";
    public static final String SPEND_COL_NEC = "SPENDING_NECESSITY";
    public static final String SPEND_COL_FRS = "SPENDING_FRSAVING";
    public static final String SPEND_COL_DT = "SPENDING_DATETIME";


    public DatabaseHelper(@Nullable Context context) {
        super(context, "coinData.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Success", "Started making income table");
        makeIncomeTable(db);
        Log.d("Success", "Started making saving table");
        makeSavingTable(db);
        Log.d("Success", "Started making spending table");
        makeSpendingTable(db);
        Log.d("Success", "Done making all tables");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void makeIncomeTable(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + INCOME_TABLE + " ("
                + INCOME_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + INCOME_COL_DESC + " TEXT, "
                + INCOME_COL_AMNT + " INT, "
                + INCOME_COL_INC + " INT)";
        db.execSQL(createTableStatement);
    }

    public void makeSavingTable(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + SAVE_TABLE + " ("
                + SAVE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SAVE_COL_DESC + " TEXT, "
                + SAVE_COL_LIMT + " INTEGER, "
                + SAVE_COL_STOR + " INTEGER, "
                + SAVE_COL_WEEK + " INTEGER, "
                + SAVE_COL_PERC + " REAL, "
                + SAVE_COL_REMV + " INTEGER )";
        db.execSQL(createTableStatement);
    }

    public void makeSpendingTable(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + SPEND_TABLE + " ("
                + SPEND_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SPEND_COL_DESC + " TEXT, "
                + SPEND_COL_AMNT + " INT, "
                + SPEND_COL_NEC + " INTEGER, "
                + SPEND_COL_FRS + " INTEGER, "
                + SPEND_COL_DT + " TEXT)";
        db.execSQL(createTableStatement);
    }


    /*********************************************************************************************
    Income Table Functions
     ********************************************************************************************/

    public List<Income> getAllIncome(Boolean a) {
        List<Income> ret = new ArrayList<>();
        String query;
        if(a)
            query = "SELECT * FROM "+INCOME_TABLE+" WHERE "+INCOME_COL_INC+" = 1";
        else
            query = "SELECT * FROM "+INCOME_TABLE+" WHERE "+INCOME_COL_INC+" = -1";
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

    public List<Income> getAllIncome() {
        List<Income> ret = new ArrayList<>();
        String query = "SELECT * FROM "+INCOME_TABLE;

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

    public boolean addOneIncome(Income i) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();

        c.put(INCOME_COL_DESC, i.getDesc());
        c.put(INCOME_COL_AMNT, i.getAmountPerWeek());
        c.put(INCOME_COL_INC, i.getInc());
        return db.insert(INCOME_TABLE, null, c) != -1;
    }

    public boolean removeOneIncome(Income i) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + INCOME_TABLE + " WHERE " + INCOME_COL_ID + " = " + i.getId();
        Cursor c = db.rawQuery(query, null);

        return c.moveToFirst();
    }

    public boolean editOneIncome(Income i, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + INCOME_TABLE +
                " SET " + INCOME_COL_AMNT + " = " + i.getAmountPerWeek() + "" +
                ", " + INCOME_COL_DESC + " = '" + i.getDesc() + "' " +
                " WHERE " + INCOME_COL_ID + " = " + id;
        db.execSQL(query);
        return true;
    }

    /*********************************************************************************************
     Saving Table Functions
     ********************************************************************************************/

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


    public boolean addToSavings(Saving s, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + SAVE_TABLE +
                " SET " + SAVE_COL_STOR + " = " + (s.getAmountStored()+amount) +
                " WHERE " + SAVE_COL_ID + " = " + s.getId();
        db.execSQL(query);
        return true;
    }

    public int updateSavingAmounts(Context c) {
        int left = Databases.getWeeklyAfterExpenses();
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
        return left;
    }

    /*********************************************************************************************
     Spending Table Functions
     ********************************************************************************************/

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
                int frs = c.getInt(4);
                String dt = c.getString(5);
                Spending s = new Spending(id, desc, amount, nec==1, dt, frs==1);
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
                int frs = c.getInt(4);
                String dt = c.getString(5);
                Spending s = new Spending(id, desc, amount, nec==1, dt, frs==1);
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
                int frs = c.getInt(4);
                String dt = c.getString(5);
                Spending s = new Spending(id, desc, amount, nec==1, dt, frs==1);
                ret.add(s);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return ret;
    }

    public List<Spending> getAllSpendFromWA(boolean after, String dateTime) {
        List<Spending> ret = new ArrayList<>();
        if(dateTime==null)
            return ret;

        String query;
        if(after)
            query = "SELECT * FROM "+SPEND_TABLE+" WHERE "+SPEND_COL_DT+" >= '"+ dateTime + "' AND "+SPEND_COL_ID+"!= 1 AND "+SPEND_COL_FRS+"!= 1";
        else
            query = "SELECT * FROM "+SPEND_TABLE+" WHERE "+SPEND_COL_DT+" < '"+ dateTime + "' AND "+SPEND_COL_ID+"!= 1 AND "+SPEND_COL_FRS+"!= 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String desc = c.getString(1);
                int amount = c.getInt(2);
                int nec = c.getInt(3);
                int frs = c.getInt(4);
                String dt = c.getString(5);
                Spending s = new Spending(id, desc, amount, nec==1, dt, frs==1);
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
        c.put(SPEND_COL_FRS, s.getFromSaving());
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
                    ", " + SPEND_COL_FRS + " = " + s.getFromSaving() + " " +
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
