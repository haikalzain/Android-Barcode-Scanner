package com.haikalzain.inventorypro.utils;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by haikalzain on 6/01/15.
 */
public class FileUtils {
    public static boolean isFileNameValid(String fileName){ //Not perfect but will do
        //Dot also banned to make things easy
        final String[] ReservedChars = {"|", "\\", "?", "*", "<", "\"", ":", ">", "."};


        for(String c :ReservedChars){
            if(fileName.contains(c)){
                return false;
            }
        }

        return true;
    }

    public static File getTemplatesDirectory(Context context){
        return new File(context.getFilesDir() + File.separator + "/templates");
    }

    public static File getSpreadsheetsDirectory(Context context){
        return new File(context.getFilesDir() + File.separator + "/spreadsheets");
    }

    public static List<File> getTemplateFiles(Context context){
        return new ArrayList<>(Arrays.asList(getTemplatesDirectory(context).listFiles()));
    }

    public static List<File> getSpreadsheetFiles(Context context){
        return new ArrayList<>(Arrays.asList(getSpreadsheetsDirectory(context).listFiles()));
    }

    public static List<String> getFileNamesWithoutExt(List<File> files){
        List<String> names = new ArrayList<>();
        for(File f: files){
            names.add(getFileNameWithoutExt(f));
        }
        return names;
    }

    public static List<String> getFileNames(List<File> files){
        List<String> names = new ArrayList<>();
        for(File f: files){
            names.add(f.getName());
        }
        return names;
    }

    public static String getFileNameWithoutExt(File file){
        int last = file.getName().lastIndexOf(".");
        return file.getName().substring(0, last);
    }

    public static String getFileNameWithoutExt(String fileName){
        int last = fileName.lastIndexOf(".");
        return fileName.substring(0, last);
    }



}
