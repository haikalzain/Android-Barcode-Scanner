package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.haikalzain.inventorypro.common.FieldType;

/**
 * Created by haikalzain on 6/01/15.
 */
public class TextFieldView extends FieldView {

    private EditText editText;

    public TextFieldView(Context context, String label, boolean isFilterView) {
        super(context, label, isFilterView);
    }

    public TextFieldView(Context context, String label) {
        super(context, label);
    }

    @Override
    protected View createInputView(Context context) {
        editText = new EditText(context);
        return editText;
    }

    @Override
    public void setInputViewValue(String dataString) {
        editText.setText(dataString);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.TEXT;
    }

    @Override
    protected String getInputDataString() {
        return editText.getText().toString();
    }
}
