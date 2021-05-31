package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

public class MainImport extends AppCompatActivity {

    private DatabaseHelper dBHelper;
    private final int CHOOSE_PDF_FROM_DEVICE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_import);

        dBHelper = Databases.getDBHelper();

    }

    public void chooseFile(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.setType("text/csv");
        startActivityForResult(intent, CHOOSE_PDF_FROM_DEVICE);
    }

    public void parseData(Uri uriPath) {

        try{
            InputStream input = getContentResolver().openInputStream(uriPath);
            BufferedReader in = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
            String line = "";
            try {
                line = in.readLine();
                if(line.equals("Income ID,Income Description,Income Amount,Income Increment,,,")) {
                    while(!(line = in.readLine()).equals(",,,,,,")) {
                        String[] val = line.split(",");
                        Income i = new Income(Integer.parseInt(val[0]),
                                val[1],
                                (int)(Double.parseDouble(val[2])*100),
                                Integer.parseInt(val[3]));
                        dBHelper.addOneIncome(i);
                    }
                }
                Log.d("Success", "Parsed Income");
                line = in.readLine();
                if(line.equals("Saving ID,Saving Description,Saving Limit Amount,Saving Amount Stored,Saving Amount per Week,Saving Percent,Saving Removable")) {
                    while(!(line = in.readLine()).equals(",,,,,,")) {
                        String[] val = line.split(",");
                        Saving s = new Saving(Integer.parseInt(val[0]),
                                val[1],
                                (Double.parseDouble(val[2])==Long.MAX_VALUE?
                                        Long.MAX_VALUE : (long)(Double.parseDouble(val[2])*100)),
                                (long)(Double.parseDouble(val[3])*100),
                                (int)(Double.parseDouble(val[4])*100),
                                Double.parseDouble(val[5]),
                                (val[6].equals("TRUE")?1:0));
                        dBHelper.addOneSave(s);
                    }
                }
                Log.d("Success", "Parsed Saving");
                line = in.readLine();
                if(line.equals("Record ID,Record Start DateTime,Record Amount,,,,")) {
                    while(!(line = in.readLine()).equals(",,,,,,")) {
                        String[] val = line.split(",");
                        SpentRecord s = new SpentRecord(Integer.parseInt(val[0]),
                                val[1],
                                (int)(Double.parseDouble(val[2])*100));
                        dBHelper.addOneRecord(s);
                    }
                }
                Log.d("Success", "Parsed Record");
                line = in.readLine();
                if(line.equals("Spending ID,Spending Description,Spending Amount,Spending Necessity,Spending From Savings,Spending DateTime,")) {
                    while(!(line = in.readLine()).equals(",,,,,,")) {
                        String[] val = line.split(",");
                        Spending s = new Spending(Integer.parseInt(val[0]),
                                val[1],
                                (int)(Double.parseDouble(val[2])*100),
                                val[3].equals("TRUE"),
                                val[5],
                                val[4].equals("TRUE"));
                        dBHelper.addOneSpend(s);
                    }
                }
                Log.d("Success", "Parsed Spending");
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.d("Success", e.toString()+"");
                Toast.makeText(this, "Unable to parse file.", Toast.LENGTH_LONG).show();

            }
            in.close();
            input.close();
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "No file found.", Toast.LENGTH_LONG).show();
        }

        goToLoading();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if(requestCode==CHOOSE_PDF_FROM_DEVICE && resultCode== RESULT_OK) {

            if(resultData!=null) {
                String filePath;
                Log.d("Success", resultData.getDataString());
                try{
                    Uri uriPath = resultData.getData();
                    parseData(uriPath);
                }catch(Exception e){
                    Log.e("Err", e.toString()+"");
                }
            }
        }
    }

    public void goToParam(View v) {
        Spending initialDate = new Spending(-1, "Last Date & Time", 0, true);
        dBHelper.addOneSpend(initialDate);
        Intent leaveActivity = new Intent(this, MainParamCheck.class);
        startActivity(leaveActivity);
    }

    public void goToLoading() {
        Intent leaveActivity = new Intent(this, MainLoading.class);
        startActivity(leaveActivity);
    }


    @Override
    public void onBackPressed() { }

}