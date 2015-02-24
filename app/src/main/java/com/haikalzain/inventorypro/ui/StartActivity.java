package com.haikalzain.inventorypro.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haikalzain.inventorypro.BuildConfig;
import com.haikalzain.inventorypro.R;

/**
 * Created by haikalzain on 15/01/15.
 */

public class StartActivity extends Activity {
    private LinearLayout linearLayout;
    private LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        linearLayout = (LinearLayout)findViewById(R.id.linear_layout);
        inflater = LayoutInflater.from(this);


        addMenuItem(R.drawable.excel, "Spreadsheets", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, SpreadsheetsActivity.class);
                startActivity(intent);
            }
        });

        addMenuItem(R.drawable.template, "Templates", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, TemplatesActivity.class);
                startActivity(intent);
            }
        });

        addMenuItem(R.drawable.sync, "Sync", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, SyncActivity.class);
                startActivity(intent);
            }
        });

        if(BuildConfig.IS_FREE){
            addMenuItem(R.drawable.upgrade, "Upgrade", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=com.haikalzain.inventorypro"));
                    startActivity(intent);
                }
            });
        }

        addMenuItem(R.drawable.about, "About", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        // Ask about reviews
        final SharedPreferences prefs = getSharedPreferences("reviews", Context.MODE_PRIVATE);
        int runs = prefs.getInt("RUNS", 0);
        Log.v("runs", ""+runs);
        boolean rated = prefs.getBoolean("RATED", false);
        if(runs != 0 && runs % 10 == 0 && !rated){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Rate InventoryScan")
                    .setMessage("If you liked InventoryScan, please support us by rating it!")
                    .setNegativeButton("Not now", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String url;
                            if(BuildConfig.IS_FREE){
                                url = "http://www.amazon.com/gp/mas/dl/android?p=com.haikalzain.inventoryfree";
                            }
                            else{
                                url = "http://www.amazon.com/gp/mas/dl/android?p=com.haikalzain.inventorypro";
                            }
                            prefs.edit().putBoolean("RATED", true).commit();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        }
                    });
            builder.create().show();

        }
        prefs.edit().putInt("RUNS", runs + 1).commit();
    }



    private void addMenuItem(int imageResource, String text, View.OnClickListener listener){
        View itemView;
        itemView = inflater.inflate(R.layout.start_menu_item, linearLayout, false);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.image_view);
        TextView textView = (TextView)itemView.findViewById(R.id.text_view);
        imageView.setImageResource(imageResource);
        textView.setText(text);
        itemView.setOnClickListener(listener);
        linearLayout.addView(itemView);
    }
}
