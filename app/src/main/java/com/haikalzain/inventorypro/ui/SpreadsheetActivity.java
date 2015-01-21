package com.haikalzain.inventorypro.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.haikalzain.inventorypro.App;
import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.common.Field;
import com.haikalzain.inventorypro.common.FieldHeader;
import com.haikalzain.inventorypro.common.Item;
import com.haikalzain.inventorypro.common.Spreadsheet;
import com.haikalzain.inventorypro.common.conditions.Condition;
import com.haikalzain.inventorypro.ui.dialogs.BarcodeDialog;
import com.haikalzain.inventorypro.ui.dialogs.SortDialog;
import com.haikalzain.inventorypro.ui.widgets.FieldView;
import com.haikalzain.inventorypro.ui.widgets.FieldViewFactory;
import com.haikalzain.inventorypro.utils.DropboxUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpreadsheetActivity extends Activity {

    private static final String TAG = "com.haikalzain.inventorypro.ui.SpreadsheetActivity";

    private static final int NEW_ITEM_REQUEST = 1;
    private static final int FILTER_REQUEST = 2;
    private static final int EDIT_ITEM_REQUEST = 3;
    private static final int SCAN_REQUEST = 4;

    public static final String SPREADSHEET = "SPREADSHEET";
    public static final String EXCEL_FILE = "EXCEL_FILE";
    public static final String CURRENT_ITEM = "CURRENT_ITEM";

    private App app;



    private ListView itemListView;
    private Spreadsheet spreadsheet = null; //since only 1 of this activity running at a time
    private File excelFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spreadsheet);

        app = (App)getApplication();
        spreadsheet = app.currentSpreadsheet;
        excelFile = app.currentExcelFile;

        Log.v(TAG, "Recreating activity");


        itemListView = (ListView)findViewById(R.id.list_view);
        updateItemListView();

        Button newItemBtn = (Button)findViewById(R.id.btn_1);
        final PopupMenu newItemMenu = new PopupMenu(this, newItemBtn);
        newItemMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "By scanning");
        newItemMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Manually");
        newItemMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case 1:
                        intent = new Intent(SpreadsheetActivity.this, ScanActivity.class);
                        startActivityForResult(intent, SCAN_REQUEST);
                        break;
                    case 2:
                        showBarcodeDialog();
                        break;
                }
                return false;
            }
        });
        newItemBtn.setText("Edit");
        newItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newItemMenu.show();
            }
        });

        Button sortBtn = (Button)findViewById(R.id.btn_2);
        sortBtn.setText("Sort");
        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SortDialog sortDialog = new SortDialog(
                        SpreadsheetActivity.this,
                        spreadsheet.getSortByOptions(),
                        spreadsheet.getSortBy(),
                        spreadsheet.getSortIsAscending());
                sortDialog.setOnPositiveButtonClicked(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spreadsheet.setSortBy(
                                sortDialog.getSelectedField(),
                                sortDialog.getIsAscending());
                        updateItemListView();
                    }
                });
                sortDialog.show();
            }
        });

        Button filterBtn = (Button)findViewById(R.id.btn_3);
        filterBtn.setText("Filter");
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpreadsheetActivity.this, FilterActivity.class);
                intent.putExtra(
                        FilterActivity.INIT_FILTER_CONDITIONS,
                        new ArrayList<>(spreadsheet.getFilterConditions()));
                intent.putExtra(
                        FilterActivity.INIT_FILTER_ITEMS,
                        new ArrayList<>(spreadsheet.getFilterItems()));
                startActivityForResult(intent, FILTER_REQUEST);
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
                saveSpreadsheet();
            }
        }
        else if(requestCode == EDIT_ITEM_REQUEST){
            if(resultCode == RESULT_OK){
                ArrayList<String> item =
                        (ArrayList<String>)data.getSerializableExtra(NewItemActivity.ITEM);
                spreadsheet.deleteItem(app.currentItem);
                spreadsheet.addItem(item);
                saveSpreadsheet();
            }
        }
        else if(requestCode == FILTER_REQUEST){
            if(resultCode == RESULT_OK){
                ArrayList<Condition> filterConditions =
                        (ArrayList<Condition>)data.getSerializableExtra(
                                FilterActivity.FILTER_CONDITIONS);
                ArrayList<String> filterItems =
                        (ArrayList<String>)data.getSerializableExtra(FilterActivity.FILTER_ITEMS);
                spreadsheet.setFilters(filterConditions, filterItems);
                updateItemListView();
            }
        }
    }

    private void updateItemListView(){
        AsyncTask<String, Integer, Long> refreshTask = new AsyncTask<String, Integer, Long>() {
            private ProgressDialog dialog;
            private ArrayAdapter<Item> adapter;
            private List<Item> sortedFilteredItemList;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(SpreadsheetActivity.this);
                dialog.setMessage("Refreshing spreadsheet");
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Long doInBackground(String... params) {
                sortedFilteredItemList = spreadsheet.getSortedFilteredItemList();
                adapter = new ItemsAdapter(
                        SpreadsheetActivity.this,
                        android.R.layout.simple_list_item_1,
                        sortedFilteredItemList);
                return 0l;
            }

            @Override
            protected void onPostExecute(Long aLong) {
                itemListView.setAdapter(adapter);
                itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Item item = adapter.getItem(position);
                        //startEditItemActivity(item);
                    }
                });
                setTitle(excelFile.getName() + " - " + sortedFilteredItemList.size() + " item(s)");
                if(dialog.isShowing())
                    dialog.dismiss();
            }
        };
        refreshTask.execute();
    }

    private class ItemsAdapter extends ArrayAdapter<Item>{

        private class ViewHolder{
            LinearLayout linearLayout;
            LinearLayout glassView;
        }


        public ItemsAdapter(Context context, int resource, List<Item> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_item, parent, false);
                viewHolder.linearLayout = (LinearLayout)convertView.findViewById(R.id.linear_layout);
                viewHolder.glassView = (LinearLayout)convertView.findViewById(R.id.glass_view);
                convertView.setTag(viewHolder);

            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            final Item item = getItem(position);

            viewHolder.linearLayout.removeAllViews();
            for(Field field: item){
                LinearLayout horizontalLayout = new LinearLayout(getContext());
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

                TextView labelView = new TextView(getContext());
                labelView.setTextAppearance(getContext(),
                        android.R.style.TextAppearance_DeviceDefault_Medium);
                labelView.setText(field.getName() + ":");
                labelView.setPadding(0, 0, 10, 0);
                labelView.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f));
                horizontalLayout.addView(labelView);

                TextView valueView = new TextView(getContext());
                valueView.setTextAppearance(getContext(),
                        android.R.style.TextAppearance_DeviceDefault_Medium);
                valueView.setText(field.getValue());
                valueView.setBackgroundColor(0x87DFFAFF);
                valueView.setPadding(30, 5, 30, 10);

                horizontalLayout.addView(valueView, new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        2f));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 30);
                viewHolder.linearLayout.addView(horizontalLayout, params);
            }

            final PopupMenu deleteItemMenu = new PopupMenu(getContext(), convertView);
            deleteItemMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Delete");
            deleteItemMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case 1:
                            spreadsheet.deleteItem(item);
                            saveSpreadsheet();

                            break;
                    }
                    return false;
                }
            });

            viewHolder.glassView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEditItemActivity(item);
                }
            });

            viewHolder.glassView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleteItemMenu.show();
                    return true;
                }
            });
            return convertView;
        }
    }

    private void saveSpreadsheet(){
        AsyncTask<String, Integer, Long> saveFileTask = new AsyncTask<String, Integer, Long>() {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(SpreadsheetActivity.this);
                dialog.setMessage("Saving " + excelFile.getName());
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Long doInBackground(String... params) {

                try {
                    spreadsheet.exportExcelToFile(excelFile);
                    DropboxUtils.copyInFile(getApplicationContext(), excelFile,
                            DropboxUtils.getSpreadsheetsPath(getApplicationContext()));
                } catch (IOException e) {
                    Log.v(TAG, "Failed to save spreadsheet");
                }
                return 0l;
            }

            @Override
            protected void onPostExecute(Long aLong) {
                if(dialog.isShowing())
                    dialog.dismiss();
                updateItemListView();
            }
        };
        saveFileTask.execute();
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

    private void showBarcodeDialog(){
        final BarcodeDialog dialog = new BarcodeDialog(this);
        dialog.setOnPositiveButtonClicked(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                String barcode = dialog.getBarcode();
                Item item = spreadsheet.getItem(barcode);

                if(item == null) {
                    startNewItemActivity(barcode);
                }
                else{
                    startEditItemActivity(item);
                }
            }
        });
        dialog.show();
    }

    private void startEditItemActivity(Item item){
        app.currentItem = item;
        Intent intent = new Intent(SpreadsheetActivity.this, NewItemActivity.class);
        intent.putExtra(NewItemActivity.INIT_VALUES, getValues(item));
        intent.putExtra(NewItemActivity.IS_EDITING, true);
        startActivityForResult(intent, EDIT_ITEM_REQUEST);
    }

    private void startNewItemActivity(String barcode){
        Intent intent = new Intent(SpreadsheetActivity.this, NewItemActivity.class);
        ArrayList<String> values = getDefaultValues();
        values.set(0, barcode);
        intent.putExtra(NewItemActivity.INIT_VALUES, values);
        startActivityForResult(intent, NEW_ITEM_REQUEST);
    }
}
