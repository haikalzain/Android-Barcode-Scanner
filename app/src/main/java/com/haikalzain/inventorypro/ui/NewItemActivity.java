package com.haikalzain.inventorypro.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.common.FieldType;
import com.haikalzain.inventorypro.common.Spreadsheet;
import com.haikalzain.inventorypro.ui.widgets.FieldView;
import com.haikalzain.inventorypro.ui.widgets.FieldViewFactory;

import java.util.ArrayList;
import java.util.List;

public class NewItemActivity extends Activity {

    public static final String ITEM = "ITEM";

    private LinearLayout contentLayout;
    private List<FieldType> fieldTypes;
    private List<String> fieldNames;
    private List<FieldView> fieldViews;
    private Spreadsheet spreadsheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_two_btn);

        fieldViews = new ArrayList<>();
        spreadsheet = SpreadsheetActivity.spreadsheet;

        fieldTypes = spreadsheet.getHeader().getFieldTypes();
        fieldNames = spreadsheet.getHeader().getFieldNames();

        Button cancelBtn = (Button)findViewById(R.id.btn_1);
        cancelBtn.setText("Cancel");
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button doneBtn = (Button)findViewById(R.id.btn_2);
        doneBtn.setText("Done");
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(ITEM, getItem());
                setResult(RESULT_OK, result);
                finish();
            }
        });

        contentLayout = (LinearLayout)findViewById(R.id.main_layout);
        createContentLayout();
        
        setTitle("New Item");
    }

    private void createContentLayout(){
        for(int i = 0; i < fieldTypes.size(); i++){
            FieldView fieldView =
                    FieldViewFactory.createFieldViewForType(
                            this,
                            fieldTypes.get(i),
                            fieldNames.get(i));
            contentLayout.addView(fieldView);
            fieldViews.add(fieldView);
        }
    }

    private ArrayList<String> getItem(){
        ArrayList<String> list = new ArrayList<>();
        for(FieldView view: fieldViews){
            list.add(view.getDataString());
        }
        return list;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_item, menu);
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
