package com.haikalzain.inventorypro.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

            }
        });

        addMenuItem(R.drawable.ic_launcher, "Email", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
