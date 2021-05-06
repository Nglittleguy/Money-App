package com.example.money;

public class Saving {
    private int id;
    private String desc;
    private long limitStored;
    private long amountStored;
    private int canTakeFrom;

    private double percent;
    private int amountPerWeek;

    public Saving(int id, String desc, long limitStored, long amountStored, double percent, int weeklyLeft, int canTakeFrom) {
        //If long term savings: limitStored = long.MAX_VALUE, and canTakeFrom = 1
        //If short term savings: limitStored != long.MAx_VALUE, and canTakeFrom = 0 (flips to 1 when limitStored = amountStored
        this.id = id;
        this.desc = desc;
        this.limitStored = limitStored;
        this.amountStored = amountStored;
        this.percent = percent;
        this.amountPerWeek = (int)(weeklyLeft * this.percent);
        this.canTakeFrom = canTakeFrom;
    }

    public Saving(int id, String desc, long limitStored, long amountStored, int amountPerWeek, double percent, int canTakeFrom) {
        this.id = id;
        this.desc = desc;
        this.limitStored = limitStored;
        this.amountStored = amountStored;
        this.canTakeFrom = canTakeFrom;
        this.percent = percent;
        this.amountPerWeek = amountPerWeek;
    }

    public Saving(int id, String desc, long limitStored, long amountStored, int amountPerWeek, int canTakeFrom) {
        //If long term savings: limitStored = long.MAX_VALUE, and canTakeFrom = 1
        //If short term savings: limitStored != long.MAx_VALUE, and canTakeFrom = 0 (flips to 1 when limitStored = amountStored
        this.id = id;
        this.desc = desc;
        this.limitStored = limitStored;
        this.amountStored = amountStored;
        this.amountPerWeek = amountPerWeek;
        this.percent = 0;
        this.canTakeFrom = canTakeFrom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getLimitStored() {
        return limitStored;
    }

    public void setLimitStored(long limitStored) {
        this.limitStored = limitStored;
    }

    public long getAmountStored() {
        return amountStored;
    }

    public void setAmountStored(long amountStored) {
        this.amountStored = amountStored;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public int getAmountPerWeek() {
        return amountPerWeek;
    }

    public void setAmountPerWeek(int amountPerWeek) {
        this.amountPerWeek = amountPerWeek;
    }

    public int getCanTakeFrom() {
        return canTakeFrom;
    }

    public void setCanTakeFrom(int canTakeFrom) {
        this.canTakeFrom = canTakeFrom;
    }

    @Override
    public String toString() {
        String take;
        if(canTakeFrom == 1)
            take = "removeable";
        else
            take = "non removeable";
        if(limitStored!=Long.MAX_VALUE) {
            if(percent==0)
                return "Savings Long Term: "+id+" {"+desc+" : "+amountPerWeek+" per week, total["+amountStored+"]}";
            else
                return "Savings Long Term: "+id+" {"+desc+" : "+percent*100+"% per week, total["+amountStored+"]}";
        }
        else {
            if(percent==0)
                return "Savings Short Term: "+id+" {"+desc+" : "+amountPerWeek+" per week, total["+amountStored+"], "+take+"}";
            else
                return "Savings Short Term: "+id+" {"+desc+" : "+percent*100+"% per week, total["+amountStored+"], "+take+"}";
        }
    }
}
