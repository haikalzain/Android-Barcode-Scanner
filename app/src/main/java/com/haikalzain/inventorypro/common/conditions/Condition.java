package com.haikalzain.inventorypro.common.conditions;

/**
 * Created by haikalzain on 10/01/15.
 * Objects MUST IMPLEMENT COMPARABLE!!!
 */
public abstract class Condition {
    public Condition(){

    }

    public abstract boolean evaluate(Object a, Object b);

    @Override
    public abstract String toString();
}
