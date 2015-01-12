package com.haikalzain.inventorypro.common;

import android.util.Log;

import com.haikalzain.inventorypro.common.conditions.Condition;
import com.haikalzain.inventorypro.ui.widgets.FieldViewFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private FieldHeader sortBy;
    private boolean isAscending;
    private List<Condition> filterConditions;
    private List<String> filterItems;

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
        this.sortBy = FieldHeader.NULL;
        this.isAscending = true;
        this.header = new SpreadsheetHeader(header);
        this.filterConditions = new ArrayList<>();
        this.filterItems = new ArrayList<>();

        for(FieldHeader h: header){
            filterConditions.add(Condition.NULL);
            filterItems.add(FieldViewFactory.getDefaultValue(h.getType()));
        }


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

    public List<FieldHeader> getSortByOptions(){
        List<FieldHeader> list = new ArrayList<>();
        list.add(FieldHeader.NULL);
        list.addAll(header.getFields());
        return list;
    }

    public FieldHeader getSortBy(){
        return sortBy;
    }

    public boolean getSortIsAscending(){
        return isAscending;
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

    public List<Item> getSortedFilteredItemList(){
        List<Item> filtered = new ArrayList<>();
        for(Item item: items){
            boolean accept = true;
            for(int i = 0; i < item.getFieldCount(); i++){
                Condition condition = filterConditions.get(i);
                Log.v(TAG, "Condition: " + condition.toString());
                if(condition.toString().equals("None")){
                    continue;
                }
                Field currentField = item.getField(i);
                String compareItem = filterItems.get(i);
                if(!condition.evaluate(
                        FieldViewFactory.getObjectForFieldType(
                                currentField.getType(),
                                currentField.getValue()),
                        FieldViewFactory.getObjectForFieldType(
                                currentField.getType(),
                                compareItem)
                )){
                    accept = false;
                    break;
                }
            }
            if(accept){
                filtered.add(item);
            }
        }

        if(sortBy.getName().equals(FieldHeader.NULL.getName())){
            Log.v(TAG, "SortBy is null");
            return filtered;
        }

        Comparator<Item> comparator = new Comparator<Item>() {
            @Override
            public int compare(Item lhs, Item rhs) {
                if(isAscending)
                    return getComparableObject(lhs).compareTo(getComparableObject(rhs));
                else
                    return getComparableObject(rhs).compareTo(getComparableObject(lhs));
            }
            private Comparable getComparableObject(Item item){
                Field field = item.getField(sortBy.getName());
                Object obj = FieldViewFactory.getObjectForFieldType(
                        field.getType(), field.getValue());
                return (Comparable)obj;
            }
        };



        Collections.sort(filtered, comparator);
        //Log.v(TAG, "Sorted list: " + filtered.toString());
        return filtered;
        // TODO implement filter
    }

    //use null to unset sort
    public void setSortBy(FieldHeader fieldHeader, boolean isAscending){
        this.sortBy = fieldHeader;
        this.isAscending = isAscending;
    }

    public void setSortBy(FieldHeader fieldHeader){
        setSortBy(fieldHeader, false);
    }

    public void setFilters(List<Condition> filterConditions, List<String> filterItems){
        this.filterConditions = new ArrayList<>(filterConditions);
        this.filterItems = new ArrayList<>(filterItems);
    }

    public List<Condition> getFilterConditions(){
        return new ArrayList<>(filterConditions);
    }

    public List<String> getFilterItems(){
        return new ArrayList<>(filterItems);
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
