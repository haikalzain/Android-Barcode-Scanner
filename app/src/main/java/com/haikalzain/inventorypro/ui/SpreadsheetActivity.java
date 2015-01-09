package com.haikalzain.inventorypro.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.common.Spreadsheet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpreadsheetActivity extends ActionBarActivity {

    private static final String TAG = "com.haikalzain.inventorypro.ui.SpreadsheetActivity";

    private static final int NEW_ITEM_REQUEST = 1;

    public static final String SPREADSHEET = "SPREADSHEET";
    public static final String EXCEL_FILE = "EXCEL_FILE";


    private ListView itemListView;
    public static Spreadsheet spreadsheet = null; //since only 1 of this activity running at a time
    private File excelFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        spreadsheet = (Spreadsheet)getIntent().getSerializableExtra(SPREADSHEET);
        excelFile = (File)getIntent().getSerializableExtra(EXCEL_FILE);

        itemListView = (ListView)findViewById(R.id.list_view);
        updateItemListView();

        Button newItemBtn = (Button)findViewById(R.id.new_file_btn);
        newItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpreadsheetActivity.this, NewItemActivity.class);
                startActivityForResult(intent, NEW_ITEM_REQUEST);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spreadsheet, menu);
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
                } catch (IOException e) {
                    Log.e(TAG, "failed to update: " + excelFile.toString());
                }
                updateItemListView();
            }
        }
    }

    private void updateItemListView(){
        ArrayAdapter<List<String>> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                spreadsheet.getItemList());
        itemListView.setAdapter(adapter);
    }
}
