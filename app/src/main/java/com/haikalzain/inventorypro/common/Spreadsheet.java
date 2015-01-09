package com.haikalzain.inventorypro.common;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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

    private List<Item> items;
    private SpreadsheetHeader header;

    public static Spreadsheet createFromExcelFile(File inputFile) throws IOException{

        Workbook workbook;
        try {
            workbook = Workbook.getWorkbook(inputFile);
        } catch (BiffException e) {
            e.printStackTrace();
            throw new IOException(e);
        }

        Sheet sheet = workbook.getSheet(0);

        SpreadsheetHeader fieldsBuilder = new SpreadsheetHeader();
        for(int i = 3; i < sheet.getColumns(); i++){//!sheet.getCell(i, 0).getContents().isEmpty()
            String contents = sheet.getCell(i, 0).getContents();
            FieldHeader fieldHeader = decodeField(contents);
            fieldsBuilder.addFieldHeader(fieldHeader);
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

    public Spreadsheet(SpreadsheetHeader header){
        this.header = new SpreadsheetHeader(header);
        items = new ArrayList<>();
    }


    public void addItem(List<String> itemData){
        if(itemData.size() != header.getFieldHeaderCount()){
            throw new IllegalArgumentException("Data row incorrect size");
        }
        List<Field> fields = new ArrayList<>();
        for(int i = 0; i < itemData.size(); i++){
            Field field = new Field(header.getFieldHeader(i), itemData.get(i));
            fields.add(field);
        }
        items.add(new Item(fields));
    }

    public int getItemCount(){
        return items.size();
    }

    public Item getItem(int row){
        return items.get(row);
    }

    public List<Item> getItemList(){
        return new ArrayList<>(items);
    }

    public void deleteItem(int position){
        items.remove(position);
    }

    public void deleteItem(Item item){
        items.remove(item);
    }

    public void exportExcelToFile(File excelFile) throws IOException{
        WritableWorkbook workbook = Workbook.createWorkbook(excelFile);
        WritableSheet sheet = workbook.createSheet(SHEET_NAME, 0);

        for(int i = 0; i < header.getFieldHeaderCount(); i++){
            Label label = new Label(i, 0, encodeField(header.getFieldHeader(i)));
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

        for(int i = 0; i < items.size(); i++){
            Item item = items.get(i);
            for(int j = 0; j < item.getFieldCount(); j++){
                Label label = new Label(j, i + 1, item.getField(j).getValue());
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

    private static String encodeField(FieldHeader fieldHeader){
        return fieldHeader.getName() + " (" + fieldHeader.getType().getName() + ")";
    }

    private static FieldHeader decodeField(String encodedField){
        String[] strings = encodedField.split("[()]");
        String name = strings[0];
        FieldType type = FieldType.getFieldTypeFromString(strings[1]);

        return new FieldHeader(type, name);
    }

    public int getFieldsCount() {
        return header.getFieldHeaderCount();
    }

    public SpreadsheetHeader getHeader(){
        return new SpreadsheetHeader(header);
    }



}
