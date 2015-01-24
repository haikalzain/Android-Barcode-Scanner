package com.haikalzain.inventorypro.utils;

import com.haikalzain.inventorypro.common.conditions.Condition;
import com.haikalzain.inventorypro.common.conditions.ContainsCondition;
import com.haikalzain.inventorypro.common.conditions.EndsWithCondition;
import com.haikalzain.inventorypro.common.conditions.EqualsCondition;
import com.haikalzain.inventorypro.common.conditions.GreaterEqualCondition;
import com.haikalzain.inventorypro.common.conditions.GreaterThanCondition;
import com.haikalzain.inventorypro.common.conditions.LessEqualCondition;
import com.haikalzain.inventorypro.common.conditions.LessThanCondition;
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
            new GreaterEqualCondition(),
            new LessThanCondition(),
            new LessEqualCondition(),
            new StartsWithCondition(),
            new EndsWithCondition(),
            new ContainsCondition()
    );
    public final static List<Condition> GENERAL_FILTER_CONDITIONS = Arrays.asList(
            Condition.NULL,
            new EqualsCondition(),
            new GreaterThanCondition(),
            new GreaterEqualCondition(),
            new LessThanCondition(),
            new LessEqualCondition()
    );
}
