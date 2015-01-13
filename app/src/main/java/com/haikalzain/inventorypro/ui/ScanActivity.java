package com.haikalzain.inventorypro.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.haikalzain.inventorypro.R;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScanActivity extends Activity {

    private ZBarScannerView scannerView;
    private boolean autoFocus;
    private boolean flash;

    public static final String SCANNED_BARCODE = "SCANNED_BARCODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        autoFocus = true;
        flash = false;

        FrameLayout layout = (FrameLayout)findViewById(R.id.main_layout);

        scannerView = new ZBarScannerView(this);
        scannerView.setResultHandler(new ZBarScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {}
                //TODO call newItemActivity
            }
        });
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
        scannerView.startCamera();
        scannerView.setAutoFocus(autoFocus);
        scannerView.setFlash(flash);
    }
}
