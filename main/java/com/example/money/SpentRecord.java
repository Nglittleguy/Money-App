package com.example.money;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SpentRecord {

    private int id;
    private Date start;
    private int amount;

    public SpentRecord(int id, Date start, int amount) {
        this.id = id;
        this.start = start;
        this.amount = amount;
    }

    public SpentRecord(int id, int amount) {
        this.id = id;
        this.start = getStartOfWeek().getTime();
        this.amount = amount;
    }

    public SpentRecord(int id, String start, int amount) {
        this.id = id;
        try {
            this.start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
            this.start = getStartOfWeek().getTime();
        }
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public String getStartString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(start);
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Calendar getStartOfWeek() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c;
    }

    @Override
    public String toString() {
        return "SpentRecord{" +
                "start=" + start +
                ", amount=" + amount +
                '}';
    }
}
