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

//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("application/*");
//        String[] mimeTypes = new String[]{"application/x-binary,application/octet-stream"};
//        if (mimeTypes.length > 0) {
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        }
//
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(Intent.createChooser(intent, messageTitle), OPEN_DIRECTORY_REQUEST_CODE);
//        } else {
//            Log.d("Unable to resolve Intent.ACTION_OPEN_DOCUMENT {}");
//        }
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
                        Income i = new Income(Integer.parseInt(val[0]), val[1], (int)(Double.parseDouble(val[2])*100), Integer.parseInt(val[3]));
                        dBHelper.addOneIncome(i);
                    }
                }

                line = in.readLine();
                if(line.equals("Saving ID,Saving Description,Saving Limit Amount,Saving Amount Stored,Saving Amount per Week,Saving Percent,Saving Removable")) {
                    while(!(line = in.readLine()).equals(",,,,,,")) {
                        String[] val = line.split(",");
                        Saving s = new Saving(Integer.parseInt(val[0]), val[1], (Double.parseDouble(val[2])==Long.MAX_VALUE?Long.MAX_VALUE:(long)(Double.parseDouble(val[2])*100)), (long)(Double.parseDouble(val[3])*100), (int)(Double.parseDouble(val[2])*100), Double.parseDouble(val[5]), (val[6].equals("TRUE")?1:0));
                        dBHelper.addOneSave(s);
                    }
                }

                line = in.readLine();
                if(line.equals("Record ID,Record Start DateTime,Record Amount,,,,")) {
                    while(!(line = in.readLine()).equals(",,,,,,")) {
                        String[] val = line.split(",");
                        SpentRecord s = new SpentRecord(Integer.parseInt(val[0]), val[1], (int)(Double.parseDouble(val[2])*100));
                        dBHelper.addOneRecord(s);
                    }
                }

                line = in.readLine();
                if(line.equals("Spending ID,Spending Description,Spending Amount,Spending Necessity,Spending From Savings,Spending DateTime,")) {
                    while(!(line = in.readLine()).equals(",,,,,,")) {
                        while((line = in.readLine())!=",,,,,,") {
                            String[] val = line.split(",");
                            Spending s = new Spending(Integer.parseInt(val[0]), val[1], (int)(Double.parseDouble(val[2])*100), val[3].equals("TRUE"), val[5], val[4].equals("TRUE"));
                            dBHelper.addOneSpend(s);
                        }
                    }
                }
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

        goToParam();
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
                    //Get file extension here.

                    //filePath  = getRealPathFromURI(uriPath);
                    //String fileExtension = getFileExtension(filePath);
                    ;
                    parseData(uriPath);
                }catch(Exception e){
                    Log.e("Err", e.toString()+"");
                }

//                if (resultData != null && resultData.getData() != null) {
//                    new CopyFileToAppDirTask().execute(resultData.getData());
//                } else {
//                    Log.d("File uri not found {}");
//                }

            }
        }
        //goToParam();
    }

    public void goToParam(View v) {
        goToParam();
    }

    public void goToParam() {
        Intent leaveActivity = new Intent(this, MainParamCheck.class);
        startActivity(leaveActivity);
    }

    /* *
    https://stackoverflow.com/questions/46948136/how-to-get-file-extension-of-a-file-picked-with-intent-action-open-document
    * */
    public  String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.d("Success", "1");
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                Log.d("Success", "2");
                cursor = this.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {

                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                Log.d("Success", "4");
                Log.e("Err", e.toString()+"");
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.d("Success", "5");
            return uri.getPath();
        }
        Log.d("Success", "6");
        return null;
    }

    public String getRealPathFromURI(Uri uri) {
        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://com.android.providers.downloads.documents"), Long.valueOf(id));

        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.DownloadColumns.DATA };
            Log.d("Success", "1");
            cursor = this.getContentResolver().query(contentUri,  proj, null, null, null);
            Log.d("Success", "2");
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.DownloadColumns.DATA);
            cursor.moveToFirst();
            Log.d("Success", "3 "+cursor.getString(column_index));
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    @Override
    public void onBackPressed() { }
}