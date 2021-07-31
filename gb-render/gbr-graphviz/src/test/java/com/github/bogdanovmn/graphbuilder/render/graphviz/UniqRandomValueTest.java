package com.github.bogdanovmn.graphbuilder.render.graphviz;

import com.github.bogdanovmn.common.core.ObjCounter;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniqRandomValueTest {

    @Test
    void next() {
        Set<Integer> values = Sets.set(1, 2, 3, 5, 8, 13);
        int times = 10_000;
        UniqRandomValue<Integer> uniq = new UniqRandomValue<>(values);
        ObjCounter<Integer> statistic = executeAndReturnStatistic(uniq, times);

        assertEquals(
            values.size(),
            statistic.keys().size()
        );
        assertTrue(
            statistic.keys().containsAll(values)
        );
        statistic.keys().forEach(
            val -> {
                long count = statistic.get(val);
                int expectedValue = times / values.size();
                int lowValue = expectedValue;
                int highValue = expectedValue + 1;
                assertTrue(
                    count >= lowValue
                        && count <= highValue,
                    String.format(
                        "Must be between %d and %d but %d",
                            lowValue, highValue, count

                    )
                );
            }
        );
        statistic.keys().forEach(x -> System.out.println(x + " = " + statistic.get(x)));
    }

    private <T> ObjCounter<T> executeAndReturnStatistic(UniqRandomValue<T> uniq, int times) {
        ObjCounter<T> counter = new ObjCounter<>();
        for (int i = 0; i < times; i++) {
            counter.increment(uniq.next());
        }
        return counter;
    }
}