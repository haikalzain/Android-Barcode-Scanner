package com.haikalzain.inventorypro.ui;

import android.app.Activity;
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

    private DbxAccountManager mDbxAcctMgr;
    private static final int LINK_DROPBOX = 0;

    private TextView textView;
    private Button syncBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        textView = (TextView)findViewById(R.id.text_view);
        syncBtn = (Button)findViewById(R.id.button);
        setSyncInactive();
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
        textView.setText(
                "AutoSynced to Dropbox folder: " +
                        DropboxUtils.getSyncFolder(getApplicationContext()));
        syncBtn.setText("Unlink Dropbox");
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DropboxUtils.unlink(getApplicationContext());
                setSyncInactive();
            }
        });
    }

    private void setSyncInactive(){
        textView.setText("Not Synced");
        syncBtn.setText("Link Dropbox");
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DropboxUtils.link(getApplicationContext(), SyncActivity.this, LINK_DROPBOX);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sync, menu);
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
}
