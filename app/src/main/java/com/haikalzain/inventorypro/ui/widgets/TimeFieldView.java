package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.haikalzain.inventorypro.common.FieldType;

/**
 * Created by haikalzain on 21/01/15.
 */
public class TimeFieldView extends FieldView{
    private TimePicker timePicker;

    public TimeFieldView(Context context, String label, boolean isFilterView) {
        super(context, label, isFilterView);
    }

    @Override
    protected View createInputView(Context context) {
        timePicker = new TimePicker(context);
        timePicker.setIs24HourView(true);
        return timePicker;
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.TIME;
    }

    @Override
    protected String getInputDataString() {
        return String.format("%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute());
    }

    @Override
    protected boolean isDialog() {
        return true;
    }

    @Override
    public void setInputViewValue(String dataString) {
        String[] data = dataString.split(":");
        timePicker.setCurrentHour(Integer.parseInt(data[0]));
        timePicker.setCurrentMinute(Integer.parseInt(data[1]));
    }
}
