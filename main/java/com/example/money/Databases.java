package com.example.money;

public final class Databases {
    private static IncomeDBHelper incomeHelper;
    public static IncomeDBHelper getIncomeHelper() {
        return incomeHelper;
    }
    public static void setIncomeHelper(IncomeDBHelper i) {
        incomeHelper = i;
    }

}
