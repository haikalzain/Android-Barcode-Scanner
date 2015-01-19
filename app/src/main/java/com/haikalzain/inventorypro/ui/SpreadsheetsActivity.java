package com.haikalzain.inventorypro.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.dropbox.sync.android.DbxException;
import com.haikalzain.inventorypro.App;
import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.common.Spreadsheet;
import com.haikalzain.inventorypro.ui.dialogs.NewSpreadsheetDialog;
import com.haikalzain.inventorypro.utils.DropboxUtils;
import com.haikalzain.inventorypro.utils.FileUtils;

import java.io.File;
import java.io.IOException;


public class SpreadsheetsActivity extends Activity {
    private static final String TAG = "com.haikalzain.inventorypro.ui.SpreadsheetsActivity";

    private ListView spreadsheetListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        spreadsheetListView = (ListView)findViewById(R.id.list_view);
        updateSpreadsheetListView();

        Button newFileBtn = (Button)findViewById(R.id.new_file_btn);
        newFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewFileDialog();
            }
        });

        setTitle("Spreadsheets");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_files, menu);
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

    private void showNewFileDialog(){

        final NewSpreadsheetDialog dialog = new NewSpreadsheetDialog(
                SpreadsheetsActivity.this,
                "New Spreadsheet",
                FileUtils.getSpreadsheetFiles(this),
                FileUtils.getTemplateFiles(this));
        DialogInterface.OnClickListener acceptedListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                String fileName = dialog.getFileName();
                String templateName = dialog.getTemplateName();

                Log.v(TAG, "creating " + fileName);
                createFile(templateName, fileName);
                openFile(fileName);

            }
        };
        dialog.setOnPositiveButtonClicked(acceptedListener);
        dialog.show();
        Log.v(TAG, "dialog shown " + dialog.getFileName());
    }


    private void createFile(String templateName, String outFileName){
        File templateFile = new File(
                FileUtils.getTemplatesDirectory(this),
                templateName + ".xls"
        );

        File outFile = new File(
                FileUtils.getSpreadsheetsDirectory(this),
                outFileName + ".xls");

        Spreadsheet spreadsheet = null;
        try {
            spreadsheet = Spreadsheet.createFromExcelFile(templateFile);
        } catch (IOException e) {
            Log.e(TAG, "Couldn't read template: " + templateFile.toString());
        }

        try {
            spreadsheet.exportExcelToFile(outFile);
            DropboxUtils.copyInFile(getApplicationContext(), outFile,
                    DropboxUtils.getSpreadsheetsPath(getApplicationContext()));
        } catch (IOException e) {
            Log.e(TAG, "Couldn't create spreadsheet: " + outFile.toString());
        }
    }

    private void openFile(String fileName){
        File file = new File(
                FileUtils.getSpreadsheetsDirectory(this),
                fileName + ".xls");
        Spreadsheet spreadsheet = null;
        try {
            spreadsheet = Spreadsheet.createFromExcelFile(file);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read spreadsheet: " + file.toString());
        }
        Intent intent = new Intent(this, SpreadsheetActivity.class);

        App app = (App)getApplication();
        app.currentSpreadsheet = spreadsheet;
        app.currentExcelFile = file;

        startActivity(intent);
    }

    private void updateSpreadsheetListView(){
        AsyncTask<String, Integer, Long> syncTask = new AsyncTask<String, Integer, Long>() {
            private ProgressDialog dialog;
            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(SpreadsheetsActivity.this);
                dialog.setMessage("Syncing Files");

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancel(false);
                        finish();
                    }
                });
                dialog.show();
            }

            @Override
            protected void onPostExecute(Long aLong) {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        SpreadsheetsActivity.this,
                        android.R.layout.simple_list_item_1,
                        FileUtils.getFileNames(FileUtils.getSpreadsheetFiles(SpreadsheetsActivity.this)));
                spreadsheetListView.setAdapter(adapter);
                spreadsheetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        openFile(FileUtils.getFileNameWithoutExt(adapter.getItem(position)));
                    }
                });

                // Delete menu
                spreadsheetListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        final int pos = position;
                        final PopupMenu deleteItemMenu = new PopupMenu(SpreadsheetsActivity.this, view);
                        deleteItemMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Delete");
                        deleteItemMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case 1:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SpreadsheetsActivity.this);
                                        builder.setTitle("Delete Spreadsheet")
                                                .setMessage("Are you sure you want to delete " + adapter.getItem(pos))
                                                .setNegativeButton("Cancel", null)
                                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        FileUtils.deleteSpreadsheet(SpreadsheetsActivity.this, adapter.getItem(pos));
                                                        try{
                                                            DropboxUtils.deleteSpreadsheet(getApplicationContext(), adapter.getItem(pos));
                                                        } catch (DbxException e) {
                                                            Log.v(TAG, "Couldn't delete spreadsheet from dropbox");
                                                        }
                                                        updateSpreadsheetListView();
                                                    }
                                                });
                                        builder.create().show();
                                        break;
                                }
                                return false;
                            }
                        });
                        deleteItemMenu.show();
                        return true;
                    }
                });

            }

            @Override
            protected Long doInBackground(String... params) {
                try {
                    DropboxUtils.importAllFromDropbox(getApplicationContext());
                } catch (IOException e) {
                    finish();
                    Log.v(TAG, "Dropbox import failed");
                }
                return 0l;
            }
        };
        syncTask.execute();
    }
}
