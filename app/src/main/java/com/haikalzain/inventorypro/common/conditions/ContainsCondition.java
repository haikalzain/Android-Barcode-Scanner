package com.haikalzain.inventorypro.common.conditions;

/**
 * Created by haikalzain on 10/01/15.
 */
public class ContainsCondition extends Condition{


    @Override
    public boolean evaluate(Object a, Object b) {
        return ((String)a).contains((String) b);
    }

    @Override
    public String toString() {
        return "Contains";
    }
}
