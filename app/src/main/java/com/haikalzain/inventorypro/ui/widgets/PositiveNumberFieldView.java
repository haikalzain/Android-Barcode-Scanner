package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;
import android.view.View;
import android.widget.NumberPicker;

import com.haikalzain.inventorypro.common.FieldType;

/**
 * Created by haikalzain on 7/01/15.
 */
public class PositiveNumberFieldView extends FieldView {
    private NumberPicker numberPicker;

    public PositiveNumberFieldView(Context context, String label, boolean isFilterView) {
        super(context, label, isFilterView);
    }

    @Override
    protected View createInputView(Context context) {
        numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(1000000000);
        numberPicker.setValue(1);

        return numberPicker;
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.POSITIVE_NUMBER;
    }

    @Override
    protected String getInputDataString() {
        //TODO check integrity
        return String.valueOf(numberPicker.getValue());
    }

    @Override
    protected boolean isDialog() {
        return true;
    }

    @Override
    public void setInputViewValue(String dataString) {
        numberPicker.setValue(Integer.parseInt(dataString));
    }

    @Override
    protected String getDefaultValue(){
        return "1";
    }
}
