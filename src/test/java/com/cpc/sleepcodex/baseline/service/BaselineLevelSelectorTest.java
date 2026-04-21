package com.cpc.sleepcodex.baseline.service;

import com.cpc.sleepcodex.baseline.model.BaselineLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BaselineLevelSelectorTest {

    @Test
    void shouldSelectStepwiseLevels() {
        BaselineLevelSelector selector = new BaselineLevelSelector();

        Assertions.assertEquals(BaselineLevel.GENERIC, selector.select(0));
        Assertions.assertEquals(BaselineLevel.GENERIC, selector.select(2));
        Assertions.assertEquals(BaselineLevel.DAY_3, selector.select(3));
        Assertions.assertEquals(BaselineLevel.DAY_7, selector.select(7));
        Assertions.assertEquals(BaselineLevel.DAY_21, selector.select(21));
    }
}
