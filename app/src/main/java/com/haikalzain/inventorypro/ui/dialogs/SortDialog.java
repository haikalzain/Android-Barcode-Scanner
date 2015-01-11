package com.haikalzain.inventorypro.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.common.Field;
import com.haikalzain.inventorypro.common.FieldHeader;

import java.util.List;

/**
 * Created by haikalzain on 10/01/15.
 */
public class SortDialog {
    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    private View rootView;
    private Spinner spinner;
    private CheckBox checkBox;

    public SortDialog(Context context, List<FieldHeader> fields,
                      FieldHeader selectedField, boolean isAscending) {


        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.dialog_sort, null);

        ArrayAdapter<FieldHeader> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                fields
        );

        spinner = (Spinner)rootView.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(fields.indexOf(selectedField));

        checkBox = (CheckBox)rootView.findViewById(R.id.check_box);
        checkBox.setChecked(!isAscending);

        builder = new AlertDialog.Builder(context);
        builder.setTitle("Sort")
                .setView(rootView)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

    }

    public boolean getIsAscending(){
        return !checkBox.isChecked();
    }

    public FieldHeader getSelectedField(){
        return (FieldHeader)spinner.getSelectedItem();
    }

    public void show(){
        dialog = builder.create();
        dialog.show();
    }

    public void setOnPositiveButtonClicked(DialogInterface.OnClickListener acceptedListener){
        builder.setPositiveButton("OK", acceptedListener);
    }
}
