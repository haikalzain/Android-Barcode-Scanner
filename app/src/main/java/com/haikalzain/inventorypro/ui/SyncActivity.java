package com.haikalzain.inventorypro.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.sync.android.DbxAccountManager;
import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.utils.DropboxUtils;

import java.io.IOException;

public class SyncActivity extends Activity {
    private static String TAG = "com.haikalzain.inventorypro.ui.SyncActivity";

    private static final int LINK_DROPBOX = 0;

    private TextView statusText, folderText;
    private Button syncBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        statusText = (TextView)findViewById(R.id.status_text);
        folderText = (TextView)findViewById(R.id.folder_text);
        syncBtn = (Button)findViewById(R.id.btn_1);
        if(DropboxUtils.isLinked(getApplicationContext())){
            setSyncActive();
        }
        else{
            setSyncInactive();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LINK_DROPBOX) {
            if (resultCode == Activity.RESULT_OK) {
                // ... Start using Dropbox files.

                try {
                    DropboxUtils.setupSync(getApplicationContext());
                    setSyncActive();
                } catch (IOException e) {
                    DropboxUtils.unlink(getApplicationContext());
                    //TODO show alert dialog failed
                }

            } else {
                // ... Link failed or was cancelled by the user.
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setSyncActive(){
        statusText.setText("Sync Active");
        folderText.setText(
                "Apps/" +
                getString(R.string.app_name) + "\n" +
                DropboxUtils.getSyncFolder(getApplicationContext()));
        syncBtn.setText("Unlink Dropbox");
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SyncActivity.this);
                builder.setTitle("Unlink Dropbox")
                        .setMessage("Are you sure you want to unlink Dropbox?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DropboxUtils.unlink(getApplicationContext());
                                setSyncInactive();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create().show();

            }
        });
    }

    private void setSyncInactive(){
        statusText.setText("Not Synced");
        folderText.setText("---");
        syncBtn.setText("Link Dropbox");
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DropboxUtils.link(getApplicationContext(), SyncActivity.this, LINK_DROPBOX);
            }
        });
    }
}
