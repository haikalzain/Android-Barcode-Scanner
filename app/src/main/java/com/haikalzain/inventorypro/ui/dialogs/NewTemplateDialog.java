package com.haikalzain.inventorypro.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haikalzain on 7/01/15.
 */
public class NewTemplateDialog {
    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    private EditText fileEdit;
    private View rootView;
    private List<String> existingFileNames;

    public NewTemplateDialog(Context context, String title, List<File> existingFiles) {
        this.existingFileNames = FileUtils.getFileNamesWithoutExt(existingFiles);
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.dialog_new_template, null);
        builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setView(rootView)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        fileEdit = (EditText)rootView.findViewById(R.id.editText);
        fileEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showErrors();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setOnPositiveButtonClicked(DialogInterface.OnClickListener acceptedListener){
        builder.setPositiveButton("OK", acceptedListener);
    }

    public void show(){
        dialog = builder.create();
        dialog.show();
        setPositiveButtonEnabled(false);
    }

    public String getFileName(){
        return fileEdit.getText().toString();
    }

    private void setPositiveButtonEnabled(boolean enabled){
        if(!dialog.isShowing())
            throw(new RuntimeException("Can't set button if dialog isn't showing"));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enabled);
    }

    private void showErrors(){
        String fileName = fileEdit.getText().toString();
        boolean error = false;
        if(fileName.equals("")){
            error = true;
            fileEdit.setError("Cannot be blank");
        }
        else if(!FileUtils.isFileNameValid(fileName)){
            error = true;
            fileEdit.setError("Invalid template name");
        }
        else if(existingFileNames.contains(fileName)){
            error = true;
            fileEdit.setError("Template already exists");
        }
        else{
            fileEdit.setError(null);
        }

        setPositiveButtonEnabled(!error);
    }
}
