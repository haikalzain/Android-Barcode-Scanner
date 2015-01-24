package com.haikalzain.inventorypro.common.conditions;

/**
 * Created by haikalzain on 10/01/15.
 */
public class LessThanCondition extends Condition {
    @Override
    public boolean evaluate(Object a, Object b) {
        return ((Comparable) b).compareTo(a) > 0;
    }

    @Override
    public String toString() {
        return "Less than";
    }
}
