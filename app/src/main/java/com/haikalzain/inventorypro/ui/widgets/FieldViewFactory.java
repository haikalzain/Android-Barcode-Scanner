package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import com.haikalzain.inventorypro.common.FieldType;

/**
 * Created by haikalzain on 6/01/15.
 */
public class FieldViewFactory {

    private static final List<FieldType> fieldTypes =
            Arrays.asList(
                    FieldType.TEXT,
                    FieldType.LONG_TEXT,
                    FieldType.NUMBER,
                    FieldType.POSITIVE_NUMBER
            );

    public static FieldView createFieldViewForType(Context context, FieldType type, String name){
        switch(type){
            case TEXT:
                return new TextFieldView(context, name);
            case LONG_TEXT:
                return new LongTextFieldView(context, name);
            case DATE:
                break;
            case DAY:
                break;
            case NUMBER:
                return new NumberFieldView(context, name);
            case POSITIVE_NUMBER:
                return new PositiveNumberFieldView(context, name);
            case DECIMAL:
                break;
            case PRICE:
                break;
        }
        return null;
    }

    public static Object getObjectForFieldType(FieldType type, String dataString){
        switch(type){
            case DATE:
                break;
            case DAY:
                break;
            case NUMBER:
                return Integer.parseInt(dataString);
            case POSITIVE_NUMBER:
                return Integer.parseInt(dataString);
            case DECIMAL:
                break;
            case PRICE:
                break;
            default:
                return dataString;
        }
        return dataString;
    }

    public static List<FieldType> getFieldTypes(){ //only returns user creatable types
        return fieldTypes;
    }
}
