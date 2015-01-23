package com.haikalzain.inventorypro;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import com.haikalzain.inventorypro.common.Item;
import com.haikalzain.inventorypro.common.Spreadsheet;
import com.haikalzain.inventorypro.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by haikalzain on 7/01/15.
 */
public class App extends Application {
    private static String TAG = "com.haikalzain.inventory.Application";
    public Spreadsheet currentSpreadsheet = null;
    public File currentExcelFile = null;
    public Item currentItem = null;

    @Override
    public void onCreate() {
        super.onCreate();

        File templatesDirectory = FileUtils.getTemplatesDirectory(this);
        if(!templatesDirectory.exists()){
            boolean success = templatesDirectory.mkdir();
        }

        File spreadsheetsDirectory = FileUtils.getSpreadsheetsDirectory(this);
        if(!spreadsheetsDirectory.exists()){
            spreadsheetsDirectory.mkdir();
        }

        // Copy in spreadsheets and templates if empty
        AssetManager assetManager = getAssets();

        if(FileUtils.getSpreadsheetFiles(this).isEmpty()){
            try {
                for (String fileName : assetManager.list("spreadsheets")) {
                    Log.v(TAG, fileName);
                    InputStream in = assetManager.open("spreadsheets/" + fileName);
                    OutputStream out =
                            new FileOutputStream(new File(spreadsheetsDirectory, fileName));
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
            catch(IOException e){

            }
        }

        if(FileUtils.getTemplateFiles(this).isEmpty()){
            try {
                for (String fileName : assetManager.list("templates")) {
                    InputStream in = assetManager.open("templates/" + fileName);
                    OutputStream out =
                            new FileOutputStream(new File(templatesDirectory, fileName));
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
            catch(IOException e){

            }
        }

    }
}
