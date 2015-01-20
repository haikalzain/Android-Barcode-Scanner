package com.haikalzain.inventorypro.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.haikalzain.inventorypro.App;
import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.common.Field;
import com.haikalzain.inventorypro.common.FieldHeader;
import com.haikalzain.inventorypro.common.Item;
import com.haikalzain.inventorypro.common.Spreadsheet;
import com.haikalzain.inventorypro.common.conditions.Condition;
import com.haikalzain.inventorypro.ui.widgets.FieldViewFactory;
import com.haikalzain.inventorypro.utils.DropboxUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScanActivity extends Activity {

    private static final String TAG = "com.haikalzain.inventorypro.ui.ScanActivity";

    private static final int NEW_ITEM_REQUEST = 1;
    private static final int EDIT_ITEM_REQUEST = 3;

    private App app;

    private Spreadsheet spreadsheet;
    private File excelFile;

    private ZBarScannerView scannerView;
    private boolean autoFocus;
    private boolean flash;

    private ToggleButton autofocusBtn;
    private ToggleButton flashBtn;

    public static final String SCANNED_BARCODE = "SCANNED_BARCODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        app = (App)getApplication();
        spreadsheet = app.currentSpreadsheet;
        excelFile = app.currentExcelFile;

        autoFocus = true;
        flash = false;

        autofocusBtn = (ToggleButton)findViewById(R.id.btn_1);
        flashBtn = (ToggleButton)findViewById(R.id.btn_2);

        autofocusBtn.setChecked(autoFocus);
        flashBtn.setChecked(flash);

        autofocusBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autofocusBtn.setChecked(isChecked);
                autoFocus = isChecked;
                scannerView.setAutoFocus(isChecked);
            }
        });

        flashBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flashBtn.setChecked(isChecked);
                flash = isChecked;
                scannerView.setFlash(isChecked);
            }
        });


        FrameLayout layout = (FrameLayout)findViewById(R.id.main_layout);

        scannerView = new ZBarScannerView(this);
        layout.addView(scannerView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume(){
        super.onResume();
        startScanner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startScanner(){
        scannerView.setResultHandler(new ZBarScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {}
                String barcode = result.getContents();
                Item item = spreadsheet.getItem(barcode);

                if(item == null) {
                    startNewItemActivity(barcode);
                }
                else{
                    startEditItemActivity(item);
                }
            }
        });
        scannerView.startCamera();
        scannerView.setAutoFocus(autoFocus);
        scannerView.setFlash(flash);
    }
    private ArrayList<String> getDefaultValues() {
        ArrayList<String> list = new ArrayList<>();
        for(FieldHeader f: spreadsheet.getHeader()){
            list.add(FieldViewFactory.getDefaultValue(f.getType()));
        }
        return list;
    }

    private ArrayList<String> getValues(Item item){
        ArrayList<String> list = new ArrayList<>();
        for(Field f: item){
            list.add(f.getValue());
        }
        return list;
    }

    private void startEditItemActivity(Item item){
        app.currentItem = item;
        Intent intent = new Intent(ScanActivity.this, NewItemActivity.class);
        intent.putExtra(NewItemActivity.INIT_VALUES, getValues(item));
        intent.putExtra(NewItemActivity.IS_EDITING, true);
        startActivityForResult(intent, EDIT_ITEM_REQUEST);
    }

    private void startNewItemActivity(String barcode){
        Intent intent = new Intent(ScanActivity.this, NewItemActivity.class);
        ArrayList<String> values = getDefaultValues();
        values.set(0, barcode);
        intent.putExtra(NewItemActivity.INIT_VALUES, values);
        startActivityForResult(intent, NEW_ITEM_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NEW_ITEM_REQUEST){
            if(resultCode == RESULT_OK){
                ArrayList<String> item =
                        (ArrayList<String>)data.getSerializableExtra(NewItemActivity.ITEM);
                spreadsheet.addItem(item);
                try {
                    spreadsheet.exportExcelToFile(excelFile);
                    DropboxUtils.copyInFile(getApplicationContext(), excelFile,
                            DropboxUtils.getSpreadsheetsPath(getApplicationContext()));
                } catch (IOException e) {
                    Log.e(TAG, "failed to update: " + excelFile.toString());
                }
            }
        }
        else if(requestCode == EDIT_ITEM_REQUEST){
            if(resultCode == RESULT_OK){
                ArrayList<String> item =
                        (ArrayList<String>)data.getSerializableExtra(NewItemActivity.ITEM);
                spreadsheet.deleteItem(app.currentItem);
                spreadsheet.addItem(item);
                try {
                    spreadsheet.exportExcelToFile(excelFile);
                    DropboxUtils.copyInFile(getApplicationContext(), excelFile,
                            DropboxUtils.getSpreadsheetsPath(getApplicationContext()));
                } catch (IOException e) {
                    Log.e(TAG, "failed to update: " + excelFile.toString());
                }
            }
        }
    }
}
