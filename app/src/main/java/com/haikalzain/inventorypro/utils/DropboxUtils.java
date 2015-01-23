package com.haikalzain.inventorypro.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haikalzain on 18/01/15.
 */
public class DropboxUtils {
    private static String TAG = "com.haikalzain.inventorypro.utils.DropboxUtils";

    private static String getAppKey(Context context){
        return context.getString(R.string.dropbox_app_key);
    }

    private static String getAppSecret(Context context){
        return context.getString(R.string.dropbox_app_secret);
    }

    public static boolean isLinked(Context context){ //queries dropbox itself, not local settings
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
        String fileName = "/Data";
        DbxPath path = new DbxPath(fileName);
        int i = 1;

        fileSystem.awaitFirstSync(); // Gets file data for first time

        while(fileSystem.isFolder(path)){
            path = new DbxPath(fileName + " (" + i + ')');
            Log.v(TAG, "Trying folder: " + path.getName());
            i++;
        }

        Log.v(TAG, "Creating Folder: " + path.getName());
        fileSystem.createFolder(path);

        SharedPreferences prefs = context.getSharedPreferences("sync", Context.MODE_PRIVATE);
        prefs.edit().putString("SYNC_FOLDER", path.toString()).commit();

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

    public static List<String> getSpreadsheetFileNames(Context context)
            throws DbxException.Unauthorized {
        return getDataFileNames(context, getSpreadsheetsPath(context));
    }

    public static List<String> getTemplateFileNames(Context context)
            throws DbxException.Unauthorized {
        return getDataFileNames(context, getTemplatesPath(context));
    }

    public static List<String> getDataFileNames(Context context, String folderPath) throws DbxException.Unauthorized {
        String folder = getSyncFolder(context);
        DbxFileSystem fileSystem = getDbxFileSystem(context);
        List<DbxFileInfo> list;
        try {
            list = fileSystem.listFolder(new DbxPath(folderPath));
        }
        catch (DbxException e) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();

        for(DbxFileInfo fileInfo: list){
            if(!fileInfo.isFolder){
                String fileName = fileInfo.path.getName();
                String withoutExt = FileUtils.getFileNameWithoutExt(fileName);
                if(fileName.endsWith(".xls")){
                    result.add(fileName);
                }
            }
        }
        return result;
    }

    public static void copyInFile(Context context, File excelFile, String destDirectory)
            throws IOException {
        if(isLinked(context)) {
            DbxFileSystem fileSystem = getDbxFileSystem(context);
            DbxPath path = new DbxPath(destDirectory + '/' + excelFile.getName());
            DbxFile file;
            if (fileSystem.exists(path)) {
                file = fileSystem.open(path);
            } else {
                file = fileSystem.create(path);
            }
            file.writeFromExistingFile(excelFile, false);
            file.close();
        }
    }

    public static void importFileFromDropbox(Context context, String fromPath, File toFile) throws IOException {
        if(isLinked(context)) {
            Log.v(TAG, "Importing " + fromPath + " to " + toFile.getPath());

            DbxFileSystem fileSystem = getDbxFileSystem(context);
            DbxFile fromFile = fileSystem.open(new DbxPath(fromPath));

            InputStream in = fromFile.getReadStream();
            OutputStream out = new FileOutputStream(toFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            fromFile.close();
            out.close();

            Log.v(TAG, "Import complete: " + toFile.getPath());
        }
    }

    public static void importAllFromDropbox(Context context) throws IOException {
        if(isLinked(context)) {
            List<String> spreadsheets = FileUtils.getFileNames(FileUtils.getSpreadsheetFiles(context));
            for (String fileName : getSpreadsheetFileNames(context)) {
                if (!spreadsheets.contains(fileName)) {
                    Log.v(TAG, "Preparing to import Spreadsheet");
                    String fromPath = getSpreadsheetsPath(context) + '/' + fileName;
                    File toFile = new File(FileUtils.getSpreadsheetsDirectory(context), fileName);
                    importFileFromDropbox(context, fromPath, toFile);
                }
            }

            List<String> templates = FileUtils.getFileNames(FileUtils.getTemplateFiles(context));
            for (String fileName : getTemplateFileNames(context)) {
                if (!templates.contains(fileName)) {
                    String fromPath = getTemplatesPath(context) + '/' + fileName;
                    File toFile = new File(FileUtils.getTemplatesDirectory(context), fileName);
                    importFileFromDropbox(context, fromPath, toFile);
                }
            }
        }
    }

    public static void deleteSpreadsheet(Context context, String fileName) throws DbxException {
        if(isLinked(context)) {
            DbxPath path = new DbxPath(new DbxPath(getSpreadsheetsPath(context)), fileName);
            getDbxFileSystem(context).delete(path);
        }
    }

    public static void deleteTemplate(Context context, String fileName) throws DbxException {
        if(isLinked(context)) {
            DbxPath path = new DbxPath(new DbxPath(getTemplatesPath(context)), fileName);
            getDbxFileSystem(context).delete(path);
        }
    }

}
