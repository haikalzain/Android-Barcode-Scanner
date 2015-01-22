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

import com.haikalzain.inventorypro.App;
import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.common.FieldType;
import com.haikalzain.inventorypro.common.Spreadsheet;
import com.haikalzain.inventorypro.ui.widgets.FieldView;
import com.haikalzain.inventorypro.ui.widgets.FieldViewFactory;

import java.util.ArrayList;
import java.util.List;

public class NewItemActivity extends Activity {

    public static final String ITEM = "ITEM";
    public static final String INIT_VALUES = "INIT_VALUES";
    public static final String IS_EDITING = "IS EDITING";


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
        spreadsheet = ((App)getApplication()).currentSpreadsheet;

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

        if(getIntent().getBooleanExtra(IS_EDITING, false)){
            setTitle("Edit Item");
        }
        else{
            setTitle("New Item");
        }

        //setting initial values
        ArrayList<String> initValues =
                (ArrayList<String>)getIntent().getSerializableExtra(INIT_VALUES);
        for(int i = 0; i < fieldViews.size(); i++){
            fieldViews.get(i).setValue(initValues.get(i));
        }

    }

    private void createContentLayout(){
        for(int i = 0; i < fieldTypes.size(); i++){
            FieldView fieldView =
                    FieldViewFactory.createFieldViewForType(
                            this,
                            fieldTypes.get(i),
                            fieldNames.get(i));

            if(i == 0) //prevent barcode from being edited
                fieldView.disableInput();

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 15, 0, 0);

            contentLayout.addView(fieldView, layoutParams);
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
}
