package com.example.money;

public class Expense {
    private int id;
    private String desc;
    private int amountPerWeek;

    public Expense(int id, String desc, int amountPerWeek) {
        this.id = id;
        this.desc = desc;
        this.amountPerWeek = amountPerWeek;
    }

    public int getAmountPerWeek() {
        return amountPerWeek;
    }

    public void setAmountPerWeek(int amountPerWeek) {
        this.amountPerWeek = amountPerWeek;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Expense "+id+": {"+desc+" - "+amountPerWeek+"}";
    }
}