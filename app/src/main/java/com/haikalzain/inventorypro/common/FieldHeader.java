package com.haikalzain.inventorypro.common;

import java.io.Serializable;

/**
 * Created by haikalzain on 9/01/15.
 */
public class FieldHeader implements Serializable{
    private FieldType type;
    private String name;

    public FieldHeader(FieldType type, String name) {
        this.type = type;
        this.name = name;
    }

    public FieldType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
