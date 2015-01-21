package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import com.haikalzain.inventorypro.common.FieldType;

/**
 * Created by haikalzain on 21/01/15.
 */
public class DateFieldView extends FieldView{
    private DatePicker datePicker;

    public DateFieldView(Context context, String label, boolean isFilterView) {
        super(context, label, isFilterView);
    }

    @Override
    protected View createInputView(Context context) {
        datePicker = new DatePicker(context);
        datePicker.setCalendarViewShown(false);
        return datePicker;
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.DATE;
    }

    @Override
    protected String getInputDataString() {
        return datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
    }

    @Override
    protected boolean isDialog() {
        return true;
    }

    @Override
    public void setInputViewValue(String dataString) {
        String[] data = dataString.split("/");
        datePicker.updateDate(
                Integer.parseInt(data[2]),
                Integer.parseInt(data[1]) - 1,
                Integer.parseInt(data[0]));
    }
}
