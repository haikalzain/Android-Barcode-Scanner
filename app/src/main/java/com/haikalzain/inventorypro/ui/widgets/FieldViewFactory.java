package com.haikalzain.inventorypro.ui.widgets;

import android.content.Context;

import java.util.Arrays;
import java.util.Calendar;
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
                    FieldType.POSITIVE_NUMBER,
                    FieldType.DATE,
                    FieldType.TIME,
                    FieldType.DECIMAL,
                    FieldType.PRICE,
                    FieldType.RATING,
                    FieldType.YES_NO
            );

    public static FieldView createFieldViewForType(
            Context context, FieldType type, String name, boolean isFilter){
        switch(type){
            case TEXT:
                return new TextFieldView(context, name, isFilter);
            case LONG_TEXT:
                return new LongTextFieldView(context, name, isFilter);
            case DATE:
                return new DateFieldView(context, name, isFilter);
            case TIME:
                return new TimeFieldView(context, name, isFilter);
            case DAY:
                break;
            case NUMBER:
                return new NumberFieldView(context, name, isFilter);
            case POSITIVE_NUMBER:
                return new PositiveNumberFieldView(context, name, isFilter);
            case DECIMAL:
                return new DecimalFieldView(context, name, isFilter);
            case PRICE:
                return new PriceFieldView(context, name, isFilter);
            case RATING:
                return new RatingFieldView(context, name, isFilter);
            case YES_NO:
                return new YesNoFieldView(context, name, isFilter);
        }
        return null;
    }

    public static FieldView createFieldViewForType(Context context, FieldType type, String name){
        return createFieldViewForType(context, type, name, false);
    }

    public static Object getObjectForFieldType(FieldType type, String dataString){
        String[] data;
        switch(type){
            case DATE:
                data = dataString.split("/");
                int day = Integer.parseInt(data[0]);
                int month = Integer.parseInt(data[1]);
                int year = Integer.parseInt(data[2]);
                return year * 10000 + month * 100 + day;
            case DAY:
                break;
            case TIME:
                data = dataString.split(":");
                return 100 * Integer.parseInt(data[0]) + Integer.parseInt(data[1]);
            case NUMBER:
                return Integer.parseInt(dataString);
            case POSITIVE_NUMBER:
                return Integer.parseInt(dataString);
            case DECIMAL:
                return Double.parseDouble(dataString);
            case PRICE:
                return Double.parseDouble(dataString);
            case RATING:
                return Double.parseDouble(dataString);
            default:
                return dataString;
        }
        return dataString;
    }

    public static String getDefaultValue(FieldType type){
        switch(type){
            case DATE:
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day =  c.get(Calendar.DAY_OF_MONTH);
                return day + "/" + month + "/" + year;
            case DAY:
                break;
            case TIME:
                Calendar d = Calendar.getInstance();
                int hour = d.get(Calendar.HOUR_OF_DAY);
                int minute = d.get(Calendar.MINUTE);
                return String.format("%02d:%02d", hour, minute);
            case NUMBER:
                return "0";
            case POSITIVE_NUMBER:
                return "0";
            case DECIMAL:
                return "0.000";
            case PRICE:
                return "0.00";
            case RATING:
                return "5";
            case YES_NO:
                return "No";
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
            case TIME:
            case RATING:
            case YES_NO:
                return ConditionUtils.GENERAL_FILTER_CONDITIONS;
        }
        return ConditionUtils.STRING_FILTER_CONDITIONS;
    }

    public static List<FieldType> getFieldTypes(){ //only returns user creatable types
        return fieldTypes;
    }
}
