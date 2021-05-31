package com.example.money;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Spending {

    private int id;
    private int amount;
    private Date dateTime;
    private String desc;
    private Boolean necessity;
    private Boolean fromSaving;

    public Spending(int id, String desc, int amount, Boolean necessity) {
        this.id = id;
        this.amount = amount;
        this.desc = desc;
        this.dateTime = new Date();
        this.necessity = necessity;
        this.fromSaving = false;
    }

    public Spending(int id, String desc, int amount, Boolean necessity, Boolean fromSaving) {
        this.id = id;
        this.amount = amount;
        this.desc = desc;
        this.dateTime = new Date();
        this.necessity = necessity;
        this.fromSaving = fromSaving;
    }

    public Spending(int id, String desc, int amount, Boolean necessity, Date dateTime, Boolean fromSaving) {
        this.id = id;
        this.amount = amount;
        this.dateTime = dateTime;
        this.desc = desc;
        this.necessity = necessity;
        this.fromSaving = fromSaving;
    }

    public Spending(int id, String desc, int amount, Boolean necessity, String dateTime, Boolean fromSaving) {
        this.id = id;
        this.amount = amount;
        try {
            this.dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            this.dateTime = new Date();
        }

        this.desc = desc;
        this.necessity = necessity;
        this.fromSaving = fromSaving;
    }

    @Override
    public String toString() {
        return "Spending{" +
                "id=" + id +
                ", amount=" + amount +
                ", dateTime=" + dateTime +
                ", desc='" + desc + '\'' +
                ", necessity=" + necessity +
                ", fromSaving=" + fromSaving +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getDateTimeString() { return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(dateTime); }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getNecessity() {
        return necessity;
    }

    public void setNecessity(Boolean necessity) {
        this.necessity = necessity;
    }

    public Boolean getFromSaving() { return fromSaving; }

    public void setFromSaving(Boolean fromSaving) { this.fromSaving = fromSaving; }
}
