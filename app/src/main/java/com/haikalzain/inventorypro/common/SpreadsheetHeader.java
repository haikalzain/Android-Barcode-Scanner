package com.haikalzain.inventorypro.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by haikalzain on 9/01/15.
 *
 * Mutable!!!
 */
public class SpreadsheetHeader implements Serializable, Iterable<FieldHeader>{
    private List<FieldHeader> fieldHeaders;
    private static List<FieldHeader> PROTECTED_FIELDS = Arrays.asList(
            new FieldHeader(FieldType.TEXT, "Barcode"),
            new FieldHeader(FieldType.TEXT, "Name"),
            new FieldHeader(FieldType.POSITIVE_NUMBER, "Count"));

    public static List<FieldHeader> getProtectedFields(){
        return new ArrayList<>(PROTECTED_FIELDS);
    }

    public static int getProtectedFieldsCount(){
        return PROTECTED_FIELDS.size();
    }

    public SpreadsheetHeader(){
        this.fieldHeaders = new ArrayList<>(PROTECTED_FIELDS);
    }

    public SpreadsheetHeader(List<FieldHeader> fieldHeaders) {
        this();
        this.fieldHeaders.addAll(fieldHeaders);
    }

    public SpreadsheetHeader(SpreadsheetHeader header) {
        this.fieldHeaders = new ArrayList<>();
        for(FieldHeader field: header){
            this.fieldHeaders.add(field);
        }
    }

    public int getFieldHeaderCount(){
        return fieldHeaders.size();
    }

    public FieldHeader getFieldHeader(int position){
        return fieldHeaders.get(position);
    }

    public boolean isFieldHeaderExists(String name){
        for(FieldHeader f: fieldHeaders){
            if(f.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public void addFieldHeader(FieldHeader fieldHeader){
        for(FieldHeader f: fieldHeaders){
            if(f.getName().equals(fieldHeader.getName())){
                throw new IllegalArgumentException("Field names must be unique");
            }
        }
        fieldHeaders.add(fieldHeader);
    }

    public void removeFieldHeader(String name){
        for(FieldHeader f: PROTECTED_FIELDS){
            if(f.getName().equals(name)){
                throw new RuntimeException("Can't delete a protected field");
            }
        }
        for(FieldHeader f: fieldHeaders){
            if(f.getName().equals(name)){
                fieldHeaders.remove(f);
                return;
            }
        }
    }

    public List<FieldHeader> getFields() {
        return new ArrayList<>(fieldHeaders);
    }

    @Override
    public Iterator<FieldHeader> iterator() {
        return fieldHeaders.iterator();
    }

    public List<FieldType> getFieldTypes() {
        List<FieldType> list = new ArrayList<>();
        for(FieldHeader f: fieldHeaders){
            list.add(f.getType());
        }
        return list;
    }

    public List<String> getFieldNames() {
        List<String> list = new ArrayList<>();
        for(FieldHeader f: fieldHeaders){
            list.add(f.getName());
        }
        return list;
    }
}
