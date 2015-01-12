package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import com.haikalzain.inventorypro.common.FieldType;
import com.haikalzain.inventorypro.common.conditions.Condition;
import com.haikalzain.inventorypro.utils.ConditionUtils;

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

    public static FieldView createFieldViewForType(
            Context context, FieldType type, String name, boolean isFilter){
        switch(type){
            case TEXT:
                return new TextFieldView(context, name, isFilter);
            case LONG_TEXT:
                return new LongTextFieldView(context, name, isFilter);
            case DATE:
                break;
            case DAY:
                break;
            case NUMBER:
                return new NumberFieldView(context, name, isFilter);
            case POSITIVE_NUMBER:
                return new PositiveNumberFieldView(context, name, isFilter);
            case DECIMAL:
                break;
            case PRICE:
                break;
        }
        return null;
    }

    public static FieldView createFieldViewForType(Context context, FieldType type, String name){
        return createFieldViewForType(context, type, name, false);
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

    public static String getDefaultValue(FieldType type){
        switch(type){
            case DATE:
                break;
            case DAY:
                break;
            case NUMBER:
                return "0";
            case POSITIVE_NUMBER:
                return "1";
            case DECIMAL:
                break;
            case PRICE:
                break;
            default:
        }
        return "";
    }

    public static List<Condition> getFiltersForFieldType(FieldType type){
        switch(type){
            case DATE:
            case DAY:
            case NUMBER:
            case POSITIVE_NUMBER:
            case DECIMAL:
            case PRICE:
                return ConditionUtils.GENERAL_FILTER_CONDITIONS;
        }
        return ConditionUtils.STRING_FILTER_CONDITIONS;
    }

    public static List<FieldType> getFieldTypes(){ //only returns user creatable types
        return fieldTypes;
    }
}
