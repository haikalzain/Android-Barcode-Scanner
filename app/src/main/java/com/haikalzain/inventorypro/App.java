package com.haikalzain.inventorypro;

import android.app.Application;
import android.util.Log;

import com.haikalzain.inventorypro.utils.FileUtils;

import java.io.File;

/**
 * Created by haikalzain on 7/01/15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        File templatesDirectory = FileUtils.getTemplatesDirectory(this);
        if(!templatesDirectory.exists()){
            boolean success = templatesDirectory.mkdir();
        }

        File spreadsheetsDirectory = FileUtils.getSpreadsheetsDirectory(this);
        if(!spreadsheetsDirectory.exists()){
            spreadsheetsDirectory.mkdir();
        }

    }
}
