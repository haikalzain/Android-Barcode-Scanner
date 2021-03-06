package com.haikalzain.inventorypro.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.haikalzain.inventorypro.App;
import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.common.FieldHeader;
import com.haikalzain.inventorypro.common.FieldType;
import com.haikalzain.inventorypro.common.Spreadsheet;
import com.haikalzain.inventorypro.common.SpreadsheetHeader;
import com.haikalzain.inventorypro.common.conditions.Condition;
import com.haikalzain.inventorypro.ui.widgets.FieldView;
import com.haikalzain.inventorypro.ui.widgets.FieldViewFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haikalzain on 11/01/15.
 */
public class FilterActivity extends Activity {
    private static final String TAG = "com.haikalzain.inventorypro.ui.FilterActivity";

    private LinearLayout contentLayout;
    private ArrayList<FieldView> fieldViews;

    public static final String TEMPLATE_PARCEL = "TEMPLATE_PARCEL";
    public static final String TEMPLATE_NAME = "TEMPLATE_NAME";
    public static final String FILTER_CONDITIONS = "FILTER_CONDITIONS";
    public static final String FILTER_ITEMS = "FILTER_ITEMS";
    public static final String INIT_FILTER_CONDITIONS = "INIT_FILTER_CONDITIONS";
    public static final String INIT_FILTER_ITEMS = "INIT_FILTER_ITEMS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_two_btn);
        setTitle("Filter Items");
        contentLayout = (LinearLayout)findViewById(R.id.main_layout);
        fieldViews = new ArrayList<>();

        Button cancelBtn = (Button)findViewById(R.id.btn_1);
        Button doneBtn = (Button)findViewById(R.id.btn_2);

        cancelBtn.setText("Cancel");
        doneBtn.setText("Done");


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(FILTER_CONDITIONS, getFilterConditions());
                result.putExtra(FILTER_ITEMS, getFilterItems());
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        Spreadsheet spreadsheet = ((App)getApplication()).currentSpreadsheet;
        for(FieldHeader header: spreadsheet.getHeader()){
            FieldView fieldView = FieldViewFactory.createFieldViewForType(
                    this, header.getType(), header.getName(), true);
            contentLayout.addView(fieldView);
            fieldViews.add(fieldView);
        }

        if(getIntent().hasExtra(INIT_FILTER_CONDITIONS)){
            List<Condition> filterConditions =
                    (ArrayList<Condition>)getIntent().getSerializableExtra(INIT_FILTER_CONDITIONS);
            for(int i = 0; i < fieldViews.size(); i++){
                fieldViews.get(i).setSelectedFilterCondition(filterConditions.get(i));
            }
        }

        if(getIntent().hasExtra(INIT_FILTER_ITEMS)){
            List<String> filterItems =
                    (ArrayList<String>)getIntent().getSerializableExtra(INIT_FILTER_ITEMS);
            for(int i = 0; i < fieldViews.size(); i++){
                fieldViews.get(i).setValue(filterItems.get(i));
            }
        }
    }

    private ArrayList<Condition> getFilterConditions(){
        ArrayList<Condition> list = new ArrayList<>();
        for(FieldView fieldView: fieldViews){
            list.add(fieldView.getFilterCondition());
            Log.v(TAG, "Condition: " + fieldView.getFilterCondition().toString());
        }
        return list;
    }

    private ArrayList<String> getFilterItems(){
        ArrayList<String> list = new ArrayList<>();
        for(FieldView fieldView: fieldViews){
            list.add(fieldView.getDataString());
        }
        return list;
    }
}
