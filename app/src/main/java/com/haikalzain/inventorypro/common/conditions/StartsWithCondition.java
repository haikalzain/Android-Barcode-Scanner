package com.haikalzain.inventorypro.common.conditions;

/**
 * Created by haikalzain on 10/01/15.
 */
public class StartsWithCondition extends Condition{


    @Override
    public boolean evaluate(Object a, Object b) {
        return ((String)a).startsWith((String)b);
    }

    @Override
    public String toString() {
        return "Starts with";
    }
}
