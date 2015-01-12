package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;
import android.view.View;
import android.widget.NumberPicker;

import com.haikalzain.inventorypro.common.FieldType;


/**
 * Created by haikalzain on 6/01/15.
 */
public class NumberFieldView extends FieldView {
    private NumberPicker numberPicker;
    private final int max_value = 1000000000;
    private final int min_value = -1000000000;

    public NumberFieldView(Context context, String label, boolean isFilterView) {
        super(context, label, isFilterView);
    }

    @Override
    protected View createInputView(Context context) {
        numberPicker = new NumberPicker(context);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.valueOf(value + min_value);
            }
        });

        numberPicker.setMaxValue(max_value - min_value);
        numberPicker.setMinValue(0);
        numberPicker.setValue(-min_value);

        numberPicker.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        numberPicker.invalidate();

        return numberPicker;
    }

    @Override
    public void setInputViewValue(String dataString) {
        numberPicker.setValue(Integer.parseInt(dataString) - min_value);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.NUMBER;
    }

    @Override
    protected String getInputDataString() {
        return String.valueOf(numberPicker.getValue() + min_value);
    }

    @Override
    protected boolean isDialog() {
        return true;
    }

    @Override
    protected String getDefaultValue(){
        return "0";
    }

}
