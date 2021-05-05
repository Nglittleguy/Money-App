package com.example.money;

import java.text.DecimalFormat;

public final class Databases {
    private static IncomeDBHelper incomeHelper;
    public static IncomeDBHelper getIncomeHelper() {
        return incomeHelper;
    }
    public static void setIncomeHelper(IncomeDBHelper i) {
        incomeHelper = i;
    }

    private static ExpenseDBHelper expenseHelper;
    public static ExpenseDBHelper getExpenseHelper() {
        return expenseHelper;
    }
    public static void setExpenseHelper(ExpenseDBHelper i) {
        expenseHelper = i;
    }

    public static String centsToDollar(int wi) {
        DecimalFormat incomeToText = new DecimalFormat("0.00");
        double amount = ((double)wi)/100;
        String num = incomeToText.format(amount);
        return "$"+num;
    }

}
