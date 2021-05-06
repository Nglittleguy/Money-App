package com.example.money;

import java.text.DecimalFormat;
import java.util.List;

public final class Databases {
    private static IncomeDBHelper incomeHelper;
    public static IncomeDBHelper getIncomeHelper() {
        return incomeHelper;
    }
    public static void setIncomeHelper(IncomeDBHelper i) {
        incomeHelper = i;
    }

    private static SavingDBHelper savingHelper;
    public static SavingDBHelper getSavingHelper() { return savingHelper; }
    public static void setIncomeHelper(SavingDBHelper s) {savingHelper = s; }

    public static String centsToDollar(int wi) {
        DecimalFormat incomeToText = new DecimalFormat("0.00");
        double amount = ((double)wi)/100;
        String num = incomeToText.format(amount);
        return "$"+num;
    }

    public static int getWeeklyAfterExpenses() {
        List<Income> in = incomeHelper.getAll();
        int total=0;
        for(Income i:in) {
            total+=i.getAmountPerWeek()*i.getInc();
        }
        return total;
    }

    public static int getWeeklyAfterExpensesAfterSavings() {
        return 0;
    }

}
