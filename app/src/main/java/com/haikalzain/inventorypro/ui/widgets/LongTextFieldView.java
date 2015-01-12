package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.haikalzain.inventorypro.common.FieldType;

/**
 * Created by haikalzain on 6/01/15.
 */
public class LongTextFieldView extends FieldView{
    private EditText editText;

    public LongTextFieldView(Context context, String label, boolean isFilterView) {
        super(context, label, isFilterView);
    }

    @Override
    protected View createInputView(Context context) {
        editText = new EditText(context);
        editText.setLines(4);
        return editText;
    }

    @Override
    public void setInputViewValue(String dataString) {
        editText.setText(dataString);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.LONG_TEXT;
    }

    @Override
    protected String getInputDataString() {
        return editText.getText().toString();
    }
}
