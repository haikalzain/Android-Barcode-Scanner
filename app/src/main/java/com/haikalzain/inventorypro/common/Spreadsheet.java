package com.haikalzain.inventorypro.common;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by haikalzain on 7/01/15.
 */
public class Spreadsheet implements Serializable{
    private static final String TAG = "com.haikalzain.inventorypro.common.Spreadsheet";

    private static final String SHEET_NAME = "Sheet 1";

    private List<FieldType> fieldTypes;
    private List<String> fieldNames;

    private List<List<String>> data;

    public static Spreadsheet createFromExcelFile(File inputFile) throws IOException{

        Workbook workbook;
        try {
            workbook = Workbook.getWorkbook(inputFile);
        } catch (BiffException e) {
            e.printStackTrace();
            throw new IOException(e);
        }

        Sheet sheet = workbook.getSheet(0);

        FieldsBuilder fieldsBuilder = new FieldsBuilder();
        for(int i = 3; i < sheet.getColumns(); i++){//!sheet.getCell(i, 0).getContents().isEmpty()
            String contents = sheet.getCell(i, 0).getContents();
            FieldPair pair = decodeField(contents);
            fieldsBuilder.addField(pair.type, pair.name);
        }

        Spreadsheet spreadsheet = new Spreadsheet(fieldsBuilder);
        for(int r = 1; r < sheet.getRows(); r++){ //!sheet.getCell(0,r).getContents().isEmpty()
            List<String> item = new ArrayList<>();
            for(int c = 0; c < spreadsheet.getFieldsCount(); c++){
                item.add(sheet.getCell(c,r).getContents());
            }
            spreadsheet.addItem(item);
        }
        return spreadsheet;
    }

    private Spreadsheet(){
        data = new ArrayList<>();
        fieldTypes = new ArrayList<>();
        fieldNames = new ArrayList<>();
    }

    public Spreadsheet(FieldsBuilder builder){
        this();
        setFields(builder.getFieldTypes(), builder.getFieldNames());
    }

    private void setFields(List<FieldType> fieldTypes, List<String> fieldNames){
        this.fieldTypes = new ArrayList<>(fieldTypes);
        this.fieldNames = new ArrayList<>(fieldNames);
    }

    public void addItem(List<String> itemData){
        if(itemData.size() != fieldTypes.size()){
            throw new IllegalArgumentException("Data row incorrect size");
        }
        data.add(new ArrayList<String>(itemData));
    }

    public int getRowCount(){
        return data.size();
    }

    public List<String> getItem(int row){
        return new ArrayList<>(data.get(row));
    }

    public List<List<String>> getItemList(){
        List<List<String>> itemList = new ArrayList<>();
        for(List<String> item: data){
            itemList.add(item);
        }
        return itemList;
    }

    public void deleteItem(int row){
        data.remove(row);
    }

    public void exportExcelToFile(File excelFile) throws IOException{
        WritableWorkbook workbook = Workbook.createWorkbook(excelFile);
        WritableSheet sheet = workbook.createSheet(SHEET_NAME, 0);

        for(int i = 0; i < fieldNames.size(); i++){
            Label label = new Label(i, 0, encodeField(fieldTypes.get(i), fieldNames.get(i)));
            try{
                sheet.addCell(label);
            }
            catch (RowsExceededException e) {
                e.printStackTrace();
                throw new IOException(e);
            } catch (WriteException e) {
                e.printStackTrace();
                throw new IOException(e);
            }
        }

        for(int i = 0; i < data.size(); i++){
            List<String> item = data.get(i);
            for(int j = 0; j < item.size(); j++){
                Label label = new Label(j, i + 1, item.get(j));
                try{
                    sheet.addCell(label);
                }
                catch (RowsExceededException e) {
                    e.printStackTrace();
                    throw new IOException(e);
                } catch (WriteException e) {
                    e.printStackTrace();
                    throw new IOException(e);
                }
            }
        }

        workbook.write();
        try {
            workbook.close();
        } catch (WriteException e) {
            e.printStackTrace();
            throw new IOException(e);
        }

    }

    private static String encodeField(FieldType type, String name){
        return name + " (" + type.getName() + ")";
    }

    private static FieldPair decodeField(String encodedField){
        String[] strings = encodedField.split("[()]");
        FieldPair pair = new FieldPair();
        pair.name = strings[0];
        pair.type = FieldType.getFieldTypeFromString(strings[1]);

        return pair;
    }

    public int getFieldsCount() {
        return fieldTypes.size();
    }

    public List<FieldType> getFieldTypes() {
        return new ArrayList<>(fieldTypes);
    }

    public List<String> getFieldNames() {
        return new ArrayList<>(fieldNames);
    }

    private static class FieldPair{
        public FieldType type;
        public String name;
    }

    @Override
    public String toString() {
        return "Spreadsheet{" +
                "fieldTypes=" + fieldTypes +
                ", fieldNames=" + fieldNames +
                ", data=" + data +
                '}';
    }

    public static class FieldsBuilder{
        private List<FieldType> fieldTypes;
        private List<String> fieldNames;

        public FieldsBuilder(){
            fieldTypes = new ArrayList<>(Arrays.asList(FieldType.TEXT, FieldType.TEXT, FieldType.POSITIVE_NUMBER));
            fieldNames = new ArrayList<>(Arrays.asList("Barcode", "Name", "Count"));
        }

        public boolean isFieldExists(String fieldName){
            return fieldNames.contains(fieldName);
        }

        public void addField(FieldType fieldType, String fieldName){
            if(isFieldExists(fieldName)){
                throw new IllegalArgumentException("Field with same name already exists");
            }

            fieldTypes.add(fieldType);
            fieldNames.add(fieldName);
        }

        public void removeField(String fieldName){
            int index = fieldNames.indexOf(fieldName);
            if(index < 3){
                throw new RuntimeException("Field removal not allowed");
            }
            fieldNames.remove(index);
            fieldTypes.remove(index);
        }

        public int getFieldCount(){
            return fieldNames.size();
        }

        public List<FieldType> getFieldTypes() {
            return new ArrayList<>(fieldTypes);
        }

        public List<String> getFieldNames() {
            return new ArrayList<>(fieldNames);
        }

        public FieldType getFieldType(int index){
            return fieldTypes.get(index);
        }

        public String getFieldName(int index){
            return fieldNames.get(index);
        }
    }
}
