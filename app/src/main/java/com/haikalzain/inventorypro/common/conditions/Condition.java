package com.haikalzain.inventorypro.common.conditions;

import java.io.Serializable;

/**
 * Created by haikalzain on 10/01/15.
 * Objects MUST IMPLEMENT COMPARABLE!!!
 */
public abstract class Condition implements Serializable{
    public static final Condition NULL = new Condition() {
        @Override
        public boolean evaluate(Object a, Object b) {
            return true;
        }

        @Override
        public String toString() {
            return "None";
        }
    };

    public Condition(){

    }

    public abstract boolean evaluate(Object a, Object b);

    @Override
    public abstract String toString();

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Condition))
            return false;
        return toString().equals(o.toString());
    }
}
