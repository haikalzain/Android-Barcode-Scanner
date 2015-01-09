package com.haikalzain.inventorypro.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haikalzain on 5/01/15.
 */
public class NewSpreadsheetDialog {

    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    private EditText fileEdit;
    private View rootView;
    private Spinner templateSpinner;

    private List<String> existingFileNames;
    private List<File> templateFiles;

    public NewSpreadsheetDialog(Context context, String title, List<File> existingFiles, List<File> templateFiles) {
        this.existingFileNames = FileUtils.getFileNamesWithoutExt(existingFiles);
        this.templateFiles = new ArrayList<>(templateFiles);

        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.dialog_new_spreadsheet, null);
        templateSpinner = (Spinner)rootView.findViewById(R.id.spinner);


        List<FileWrapper> files = new ArrayList<>();
        for(File file: templateFiles){
            files.add(new FileWrapper(file));
        }
        ArrayAdapter<FileWrapper> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                files);
        templateSpinner.setAdapter(adapter);

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
            fileEdit.setError("Invalid file name");
        }
        else if(existingFileNames.contains(fileName)){
            error = true;
            fileEdit.setError("Spreadsheet already exists");
        }
        else{
            fileEdit.setError(null);
        }

        setPositiveButtonEnabled(!error);
    }

    public String getTemplateName() {
        return templateSpinner.getSelectedItem().toString();
    }

    private class FileWrapper{ //for use with arrayadapter
        public File file;
        public FileWrapper(File file){
            this.file = file;
        }

        @Override
        public String toString(){
            return FileUtils.getFileNameWithoutExt(file);
        }


    }
}
