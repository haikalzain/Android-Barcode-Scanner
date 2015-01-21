package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.haikalzain.inventorypro.common.FieldType;

import java.util.Arrays;

/**
 * Created by haikalzain on 21/01/15.
 */
public class YesNoFieldView extends FieldView{
    private Spinner spinner;
    private ArrayAdapter<String> adapter;

    public YesNoFieldView(Context context, String label, boolean isFilterView) {
        super(context, label, isFilterView);
    }

    public YesNoFieldView(Context context, String label) {
        super(context, label);
    }

    @Override
    protected View createInputView(Context context) {
        spinner = new Spinner(context);
        adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Yes", "No"));
        spinner.setAdapter(adapter);
        return spinner;
    }

    @Override
    public void setInputViewValue(String dataString) {
        if(dataString.equals("Yes")){
            spinner.setSelection(0);
        }
        else{
            spinner.setSelection(1);
        }
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.YES_NO;
    }

    @Override
    protected String getInputDataString() {
        return (String)spinner.getSelectedItem();
    }

    @Override
    protected boolean isDialog() {
        return false;
    }

}
