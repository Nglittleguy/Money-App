package com.example.money;

public class Income {
    private int id;
    private String desc;
    private int amountPerWeek;
    private int inc;

    public Income(int id, String desc, int amountPerWeek, int inc) {
        this.id = id;
        this.desc = desc;
        this.amountPerWeek = amountPerWeek;
        this.inc = inc;
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

    public int getInc() { return inc; }

    public void setInc(int a) { inc = a; }


    @Override
    public String toString() {
        if(inc==1)
            return "Income "+id+": {"+desc+" : "+amountPerWeek+"}";
        else
            return "Expense "+id+": {"+desc+" : "+amountPerWeek+"}";
    }
}
