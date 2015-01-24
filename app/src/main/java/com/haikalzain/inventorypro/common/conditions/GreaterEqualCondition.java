package com.haikalzain.inventorypro.common.conditions;

/**
 * Created by haikalzain on 10/01/15.
 */
public class GreaterEqualCondition extends Condition {
    @Override
    public boolean evaluate(Object a, Object b) {
        return ((Comparable) a).compareTo(b) >= 0;
    }

    @Override
    public String toString() {
        return "Greater than or Equal to";
    }
}
