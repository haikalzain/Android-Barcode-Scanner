package com.haikalzain.inventorypro.common;

import java.io.Serializable;

/**
 * Created by haikalzain on 9/01/15.
 */
public class Field implements Serializable{
    private final FieldHeader fieldHeader;
    private final String value;

    public Field(FieldHeader fieldHeader, String value) {
        this.fieldHeader = fieldHeader;
        this.value = value;
    }

    public FieldType getType() {
        return fieldHeader.getType();
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return fieldHeader.getName();
    }

    @Override
    public String toString() {
        return "Field{" +
                "name=" + fieldHeader.getName() +
                ", type='" + fieldHeader.getType() + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
