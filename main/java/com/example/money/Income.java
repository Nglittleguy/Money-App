package com.example.money;

public class Income extends Parameter{
    private int amountPerWeek;
    private int inc;

    public Income(int id, String desc, int amountPerWeek, int inc) {
        super(id, desc);
        this.amountPerWeek = amountPerWeek;
        this.inc = inc;
    }

    public int getAmountPerWeek() {
        return amountPerWeek;
    }

    public void setAmountPerWeek(int amountPerWeek) {
        this.amountPerWeek = amountPerWeek;
    }

    public int getInc() { return inc; }

    public void setInc(int a) { inc = a; }


    @Override
    public String toString() {
        if(inc==1)
            return "Income "+getId()+": {"+getDesc()+" : "+amountPerWeek+"}";
        else
            return "Expense "+getId()+": {"+getDesc()+" : "+amountPerWeek+"}";
    }
}
