package com.haikalzain.inventorypro.ui.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.common.FieldType;

/**
 * Created by haikalzain on 6/01/15.
 */
public abstract class FieldView extends FrameLayout {
    private static final String TAG = "com.haikalzain.inventorypro.ui.widgets.FieldView";

    private final Context context;
    private String label;
    private int orientation;
    private boolean isDialog;
    private EditText editText;
    private boolean disabledInput;

    public FieldView(Context context, String label) {
        super(context);
        this.label = label;
        this.isDialog = isDialog();
        this.context = context;
        this.disabledInput = false;


        LayoutInflater.from(context).inflate(R.layout.field_view, this);
        ((TextView)findViewById(R.id.textView)).setText(getLabel());
        FrameLayout layout = ((FrameLayout)findViewById(R.id.input_container));


        if(!isDialog){
            layout.addView(createInputView(context));
            setValue(getDefaultValue());
        }
        else{
            Log.v(TAG, "Creating dialog");
            editText = new EditText(context);
            editText.setText(getDefaultValue());
            editText.setFocusable(false);
            editText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(disabledInput)
                        return;
                    AlertDialog.Builder builder = new AlertDialog.Builder(FieldView.this.context);
                    builder.setTitle(FieldView.this.label)
                           .setView(createInputView(FieldView.this.context))
                           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {

                               }
                           })
                           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   editText.setText(getInputDataString());
                               }
                           });
                    builder.create().show();
                    setInputViewValue(editText.getText().toString());
                }
            });
            layout.addView(editText);
        }

        //noinspection ResourceType
        ((LinearLayout)findViewById(R.id.linear_layout)).setOrientation(getOrientation()); //TODO remove
    }

    protected abstract View createInputView(Context context);

    protected abstract void setInputViewValue(String dataString);

    public void setValue(String dataString){
        if(isDialog){
            editText.setText(dataString);
        }
        else{
            setInputViewValue(dataString);
        }
    }



    public String getLabel(){
        return label + ":";
    }

    public abstract FieldType getFieldType();

    protected abstract String getInputDataString();

    protected boolean isDialog(){
        return false;
    }

    protected String getDefaultValue(){
        return "";
    }

    public String getDataString(){
        if(isDialog){
            return editText.getText().toString();
        }
        else{
            return getInputDataString();
        }
    }

    public int getOrientation(){
        return LinearLayout.VERTICAL;
    }

    public void disableInput(){
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        disabledInput = true;
    }

}
