package com.haikalzain.inventorypro.common;

import java.io.Serializable;

/**
* Created by haikalzain on 7/01/15.
*/
public enum FieldType implements Serializable{
    TEXT("Text"),
    LONG_TEXT("Long Text"), //i.e. multiline
    DATE("Date"),
    TIME("Time"),
    RATING("Rating"),
    YES_NO("Yes/No"),
    DAY("Day"),
    NUMBER("Number"),
    POSITIVE_NUMBER("Positive Number"),
    DECIMAL("Decimal"),
    PRICE("Price");

    private String name;
    FieldType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return name;
    }

    public static FieldType getFieldTypeFromString(String name){
        for(FieldType type: values()){
            if(type.getName().equals(name))
                return type;
        }
        return null;
    }
}
