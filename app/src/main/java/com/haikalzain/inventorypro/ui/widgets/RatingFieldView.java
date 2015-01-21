package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.haikalzain.inventorypro.common.FieldType;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by haikalzain on 21/01/15.
 */
public class RatingFieldView extends FieldView {
    private RatingBar ratingBar;

    public RatingFieldView(Context context, String label, boolean isFilterView) {
        super(context, label, isFilterView);
    }

    public RatingFieldView(Context context, String label) {
        super(context, label);
    }

    @Override
    protected View createInputView(Context context) {
        ratingBar = new RatingBar(context);

        ratingBar.setLayoutParams(new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);
        ratingBar.setRating(0);
        FrameLayout layout = new FrameLayout(context);
        layout.addView(ratingBar);
        return layout;
    }

    @Override
    public void setInputViewValue(String dataString) {
        ratingBar.setRating(Integer.parseInt(dataString));
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.RATING;
    }

    @Override
    protected String getInputDataString() {
        return "" + (int)ratingBar.getRating();
    }

    @Override
    protected boolean isDialog() {
        return true;
    }
}
