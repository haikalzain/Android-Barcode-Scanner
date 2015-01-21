package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;

import com.haikalzain.inventorypro.common.FieldType;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by haikalzain on 21/01/15.
 */
public class DecimalFieldView extends FieldView{
    private EditText editText;

    public DecimalFieldView(Context context, String label, boolean isFilterView) {
        super(context, label, isFilterView);
    }

    public DecimalFieldView(Context context, String label) {
        super(context, label);
    }

    @Override
    protected View createInputView(Context context) {
        editText = new EditText(context);
        DecimalFormatSymbols d = DecimalFormatSymbols.getInstance(Locale.getDefault());
        final Pattern pattern = Pattern.compile("[0-9]*(\\.[0-9]*)?");

        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(!pattern.matcher(dest).matches()){
                    return "";
                }
                return null;
            }
        }});
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789" + d.getDecimalSeparator()));

        return editText;
    }

    @Override
    public void setInputViewValue(String dataString) {
        editText.setText(String.format("%01f", Double.parseDouble(dataString)));
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.DECIMAL;
    }

    @Override
    protected String getInputDataString() {
        String raw = editText.getText().toString();
        if(raw.equals("")){
            raw = getDefaultValue();
        }
        return String.format("%01f", Double.parseDouble(raw));
    }
}
