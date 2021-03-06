package com.haikalzain.inventorypro.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.common.FieldHeader;
import com.haikalzain.inventorypro.common.FieldType;
import com.haikalzain.inventorypro.common.Spreadsheet;
import com.haikalzain.inventorypro.common.SpreadsheetHeader;
import com.haikalzain.inventorypro.ui.widgets.FieldView;
import com.haikalzain.inventorypro.ui.widgets.FieldViewFactory;
import com.haikalzain.inventorypro.utils.FileUtils;

import java.util.List;

public class NewTemplateActivity extends Activity {
    private static final String TAG = "com.haikalzain.inventorypro.ui.NewTemplateActivity";

    private LinearLayout contentLayout;
    private SpreadsheetHeader fieldsBuilder;
    private String templateName;

    public static final String TEMPLATE_PARCEL = "TEMPLATE_PARCEL";
    public static final String TEMPLATE_NAME = "TEMPLATE_NAME";
    public static final String IS_EDITING = "IS_EDITING";
    public static final String HEADER = "HEADER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_three_btn);
        contentLayout = (LinearLayout)findViewById(R.id.main_layout);

        templateName = getIntent().getStringExtra(TEMPLATE_NAME);

        Button addBtn = (Button)findViewById(R.id.btn_1);
        Button cancelBtn = (Button)findViewById(R.id.btn_2);
        Button doneBtn = (Button)findViewById(R.id.btn_3);

        addBtn.setText("Add Field...");
        cancelBtn.setText("Cancel");
        doneBtn.setText("Done");

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFieldDialog();
            }
        });

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
                result.putExtra(TEMPLATE_NAME, templateName);
                result.putExtra(TEMPLATE_PARCEL, new Spreadsheet(fieldsBuilder));
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        if(getIntent().getBooleanExtra(IS_EDITING, false)){
            setTitle("Edit Template - " + templateName + ".xls");
            fieldsBuilder = (SpreadsheetHeader)getIntent().getSerializableExtra(HEADER);
        }
        else{
            setTitle("New Template - " + templateName + ".xls");
            fieldsBuilder = new SpreadsheetHeader();
        }

        List<FieldType> types = fieldsBuilder.getFieldTypes();
        List<String> names = fieldsBuilder.getFieldNames();
        for(int i = 0; i < fieldsBuilder.getFieldHeaderCount(); i++){
            boolean deletable = false;
            if(i >= SpreadsheetHeader.getProtectedFieldsCount())
                deletable = true;
            addFieldView(types.get(i), names.get(i), deletable);
        }
    }

    private void showAddFieldDialog() {
        final View rootView = getLayoutInflater().inflate(R.layout.dialog_new_field, null);

        final EditText editText = (EditText) rootView.findViewById(R.id.edit_text);
        final Spinner typeSpinner = (Spinner) rootView.findViewById(R.id.spinner);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Field")
               .setView(rootView)
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               })
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       FieldType type =
                               (FieldType)typeSpinner.getSelectedItem();
                       addField(type, editText.getText().toString());
                       Log.v(TAG, "selected type: " + type.getName());
                   }
               });


        ArrayAdapter<FieldType> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                FieldViewFactory.getFieldTypes());
        typeSpinner.setAdapter(adapter);

        final AlertDialog dialog = builder.create();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean error = false;
                String fieldName = editText.getText().toString();
                if(fieldName.equals("")){
                    error = true;
                    editText.setError("Cannot be blank");
                }
                else if(!fieldName.trim().equals(fieldName)){
                    error = true;
                    editText.setError("No leading/trailing whitespace allowed");
                }
                else if(fieldsBuilder.getFieldNames().contains(fieldName)){
                    error = true;
                    editText.setError("Field already exists");
                }
                else{
                    editText.setError(null);
                }
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!error);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
    }

    private void addField(FieldType fieldType, String name){
        addFieldView(fieldType, name, true);
        fieldsBuilder.addFieldHeader(new FieldHeader(fieldType, name));

    }

    private void addFieldView(FieldType fieldType, String name, boolean deletable){
        final FieldType fieldType1 = fieldType;
        final String name1 = name;
        final FrameLayout fieldPreview = new FrameLayout(this);
        View rootView = getLayoutInflater().inflate(R.layout.field_preview, null);
        fieldPreview.addView(rootView);

        Log.v(TAG, "adding field preview");

        final FieldView fieldView = FieldViewFactory.createFieldViewForType(this, fieldType, name);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 15);
        fieldPreview.setLayoutParams(params);

        ((FrameLayout)rootView.findViewById(R.id.container)).addView(fieldView);
        Button deleteBtn = ((Button)rootView.findViewById(R.id.delete_btn));
        deleteBtn.setEnabled(deletable);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeField(fieldType1, name1, fieldPreview);
            }
        });
        contentLayout.addView(fieldPreview);
        fieldView.disableInput();
    }

    private void removeField(FieldType fieldType, String name, View fieldView){
        fieldsBuilder.removeFieldHeader(name);
        contentLayout.removeView(fieldView);
    }
}
