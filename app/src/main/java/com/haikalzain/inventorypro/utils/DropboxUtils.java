package com.haikalzain.inventorypro.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.haikalzain.inventorypro.R;
import com.haikalzain.inventorypro.common.Spreadsheet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haikalzain on 18/01/15.
 */
public class DropboxUtils {
    private static String getAppKey(Context context){
        return context.getString(R.string.dropbox_app_key);
    }

    private static String getAppSecret(Context context){
        return context.getString(R.string.dropbox_app_secret);
    }

    public boolean isLinked(Context context){ //queries dropbox itself, not local settings
        return getDbxAccountManager(context).hasLinkedAccount();
    }

    public static String getSyncFolder(Context context){ //if "" then not synced
        SharedPreferences prefs = context.getSharedPreferences("sync", Context.MODE_PRIVATE);
        return prefs.getString("SYNC_FOLDER", "");
    }

    public static DbxAccountManager getDbxAccountManager(Context context){
        return DbxAccountManager.getInstance(context, getAppKey(context), getAppSecret(context));
    }

    public static String getTemplatesPath(Context context){
        return getSyncFolder(context) + "/templates";
    }

    public static String getSpreadsheetsPath(Context context){
        return getSyncFolder(context) + "/spreadsheets";
    }

    public static DbxFileSystem getDbxFileSystem(Context context) throws DbxException.Unauthorized {
        DbxAccountManager accm = getDbxAccountManager(context);
        return DbxFileSystem.forAccount(accm.getLinkedAccount());
    }

    public static void link(Context context, Activity activity, int callbackRequestCode)
             {
        getDbxAccountManager(context).startLink(activity, callbackRequestCode);
    }

    public static void unlink(Context context)
    {
        getDbxAccountManager(context).unlink();
        SharedPreferences prefs = context.getSharedPreferences("sync", Context.MODE_PRIVATE);
        prefs.edit().remove("SYNC_FOLDER").commit();
    }

    public static void setupSync(Context context)throws IOException {
        //Creating file system
        DbxFileSystem fileSystem = getDbxFileSystem(context);
        String fileName = "Data";
        DbxPath path = new DbxPath(fileName);
        int i = 1;
        while(fileSystem.exists(path)){
            path = new DbxPath(fileName + '(' + i + ')');
            i++;
        }
        fileSystem.createFolder(path);

        SharedPreferences prefs = context.getSharedPreferences("sync", Context.MODE_PRIVATE);
        prefs.edit().putString("SYNC_FOLDER", path.getName()).commit();

        fileSystem.createFolder(new DbxPath(path, "spreadsheets"));
        fileSystem.createFolder(new DbxPath(path, "templates"));

        //Copying files in
        for(File f: FileUtils.getSpreadsheetFiles(context)){
            copyInFile(context, f, getSpreadsheetsPath(context));
        }

        for(File f: FileUtils.getTemplateFiles(context)) {
            copyInFile(context, f, getTemplatesPath(context));
        }
    }

    public static List<String> getSpreadsheetFileNames(Context context) throws DbxException.Unauthorized {
        String folder = getSyncFolder(context);
        DbxFileSystem fileSystem = getDbxFileSystem(context);
        List<DbxFileInfo> list;
        try {
            list = fileSystem.listFolder(new DbxPath(getSpreadsheetsPath(context)));
        }
        catch (DbxException e) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();

        for(DbxFileInfo fileInfo: list){
            if(!fileInfo.isFolder){
                String fileName = fileInfo.path.getName();
                if(fileName.endsWith(".xls")){
                    result.add(FileUtils.getFileNameWithoutExt(fileName));
                }
            }
        }
        return result;
    }

    public static void copyInFile(Context context, File excelFile, String destDirectory)
            throws IOException {
        DbxFileSystem fileSystem = getDbxFileSystem(context);
        DbxPath path = new DbxPath(destDirectory + '/' +excelFile.getName());
        DbxFile file;
        if(fileSystem.exists(path)){
            file = fileSystem.open(path);
        }
        else{
            file = fileSystem.create(path);
        }
        file.writeFromExistingFile(excelFile, false);
        file.close();
    }

    public static void copyOutFile(Context context, String fromPath, File toFile){

    }

}
