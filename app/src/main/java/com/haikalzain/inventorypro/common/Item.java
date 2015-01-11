package com.haikalzain.inventorypro.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by haikalzain on 9/01/15.
 */
public class Item implements Iterable<Field>, Serializable{
    private List<Field> data;

    public Item(List<Field> data){
        this.data = new ArrayList<>(data);
    }

    public Field getField(int position){
        return data.get(position);
    }

    public Field getField(String name){
        for(Field field: data){
            if(field.getName().equals(name)){
                return field;
            }
        }
        return null;
    }

    public int getFieldCount(){
        return data.size();
    }

    @Override
    public Iterator<Field> iterator() {
        return data.iterator();
    }

    @Override
    public String toString() {
        return "Item{" +
                "data=" + data +
                '}';
    }
}
