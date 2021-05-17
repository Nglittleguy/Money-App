package com.example.money;

import java.sql.Date;

public class Spending {

    private int id;
    private int amount;
    private Date dateTime;
    private String desc;
    private Boolean necessity;

    public Spending(int id, String desc, int amount, Boolean necessity) {
        this.id = id;
        this.amount = amount;
        this.desc = desc;
        this.necessity = necessity;
    }

    public Spending(int id, String desc, int amount, Boolean necessity, Date dateTime) {
        this.id = id;
        this.amount = amount;
        this.dateTime = dateTime;
        this.desc = desc;
        this.necessity = necessity;
    }

    public Spending(int id, String desc, int amount, Boolean necessity, String dateTime) {
        this.id = id;
        this.amount = amount;
        this.dateTime = Date.valueOf(dateTime);
        this.desc = desc;
        this.necessity = necessity;
    }

    @Override
    public String toString() {
        return "Spending{" +
                "id=" + id +
                ", amount=" + amount +
                ", dateTime=" + dateTime +
                ", desc='" + desc + '\'' +
                ", necessity=" + necessity +
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
}
