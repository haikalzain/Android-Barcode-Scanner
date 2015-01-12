package com.haikalzain.inventorypro.utils;

import com.haikalzain.inventorypro.common.conditions.Condition;
import com.haikalzain.inventorypro.common.conditions.EqualsCondition;
import com.haikalzain.inventorypro.common.conditions.GreaterThanCondition;
import com.haikalzain.inventorypro.common.conditions.StartsWithCondition;

import java.util.Arrays;
import java.util.List;

/**
 * Created by haikalzain on 10/01/15.
 */
public class ConditionUtils {
    public final static List<Condition> STRING_FILTER_CONDITIONS = Arrays.asList(
            Condition.NULL,
            new EqualsCondition(),
            new GreaterThanCondition(),
            new StartsWithCondition()
    );
    public final static List<Condition> GENERAL_FILTER_CONDITIONS = Arrays.asList(
            Condition.NULL,
            new EqualsCondition(),
            new GreaterThanCondition()
    );
}
