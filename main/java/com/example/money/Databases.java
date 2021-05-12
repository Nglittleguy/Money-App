package com.example.money;

import android.util.Log;

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
    public static void setSavingHelper(SavingDBHelper s) {savingHelper = s; }

    public static String centsToDollar(int wi) {
        DecimalFormat incomeToText = new DecimalFormat("0.00");
        double amount = ((double)wi)/100;
        String num = incomeToText.format(amount);
        return "$"+num;
    }

    public static String centsToDollar(long wi) {
        DecimalFormat incomeToText = new DecimalFormat("0.00");
        double amount = ((double)wi)/100;
        String num = incomeToText.format(amount);
        return "$"+num;
    }

    public static int getWeeklyAfterExpenses() {
        if(incomeHelper!=null) {
            List<Income> in = incomeHelper.getAll();
            int total=0;
            for(Income i:in) {
                total+=i.getAmountPerWeek()*i.getInc();
            }
            return total;
        }
        return 0;
    }

    public static int getWeeklyIncome() {
        if(incomeHelper!=null) {
            List<Income> in = incomeHelper.getAll(true);
            int total=0;
            for(Income i:in) {
                total+=i.getAmountPerWeek();
            }
            return total;
        }
        return 0;
    }

    public static int getWeeklyExpenses() {
        if(incomeHelper!=null) {
            List<Income> in = incomeHelper.getAll(false);
            int total=0;
            for(Income i:in) {
                total+=i.getAmountPerWeek();
            }
            return total;
        }
        return 0;
    }

    public static int getWeeklySaving() {
        if(savingHelper!=null) {
            List<Saving> sa = savingHelper.getAllNonFinished();
            int total = 0;
            for(Saving s:sa) {
                total+=s.getAmountPerWeek();
            }
            return total;
        }
        return 0;
    }

    public static int getRemaining() {
        return getWeeklyAfterExpenses()-getWeeklySaving();
    }


    private static int province = 2;

    public static double[][][] taxCanada =
	{
		//Federal
		{{49020, 0.15}, {49020, 0.205}, {53939, 0.26}, {64533, 0.29}, {Double.MAX_VALUE, 0.33}},

		//BC
		{{42184, 0.0506}, {42185, 0.077}, {12497, 0.105}, {20757, 0.1229}, {41860, 0.147}, {62937, 0.168}, {Double.MAX_VALUE, 0.205}},

		//AB
		{{131220, 0.1}, {26244, 0.12}, {52488, 0.13}, {104976, 0.14}, {Double.MAX_VALUE, 0.15}},

		//SK
		{{45677, 0.105}, {84829, 0.125}, {Double.MAX_VALUE, 0.145}},

		//MB
		{{33723, 0.108}, {39162, 0.1275}, {Double.MAX_VALUE, 0.174}},

		//ON
		{{45142, 0.0505}, {45145, 0.0915}, {59713, 0.1116}, {70000, 0.1216}, {Double.MAX_VALUE, 0.1316}},

		//QC
		{{44545, 0.15}, {44535, 0.2}, {19310, 0.24}, {Double.MAX_VALUE, 0.2575}},

		//NL
		{{38081, 0.087}, {38080, 0.145}, {59812, 0.158}, {54390, 0.173}, {Double.MAX_VALUE, 0.183}},

		//NB
		{{43835, 0.0968}, {43836, 0.1482}, {54863, 0.1652}, {19849, 0.1784}, {Double.MAX_VALUE, 0.203}},

		//PEI
		{{31984, 0.098}, {31985, 0.138}, {Double.MAX_VALUE, 0.167}},

		//NS
		{{29590, 0.0879}, {29590, 0.1495}, {33820, 0.1667}, {57000, 0.175}, {Double.MAX_VALUE, 0.21}},

		//YK
		{{49020, 0.064}, {49020, 0.09}, {53938, 0.109}, {348022, 0.128}, {Double.MAX_VALUE, 0.15}},

		//NW
		{{44396, 0.059}, {44400, 0.086}, {55566, 0.122}, {Double.MAX_VALUE, 0.1405}},

		//NV
		{{46740, 0.04}, {46740, 0.07}, {58498, 0.09}, {Double.MAX_VALUE, 0.115}},
	};

    public static double taxAmount(double income) {
        double left = income, copy = income, tax = 0;
        if(province>13 || province<0)
            return 0;

        for(int i = 0; i<taxCanada[0].length && copy>0; i++) {
            if(copy>taxCanada[0][i][0]) {
                copy -= taxCanada[0][i][0];
                tax += taxCanada[0][i][1] * taxCanada[0][i][0];
            }
            else {
                tax += taxCanada[0][i][1] * copy;
                copy = 0;
            }
        }

        for(int i = 0; i<taxCanada[province].length && left>0; i++) {
            if(left>taxCanada[province][i][0]) {
                left -= taxCanada[province][i][0];
                tax += taxCanada[province][i][1] * taxCanada[province][i][0];
            }
            else {
                tax += taxCanada[province][i][1] * left;
                left = 0;
            }
        }

        return tax;
    }

}
