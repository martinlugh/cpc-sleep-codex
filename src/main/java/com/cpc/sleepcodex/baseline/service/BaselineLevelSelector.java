package com.cpc.sleepcodex.baseline.service;

import com.cpc.sleepcodex.baseline.model.BaselineLevel;

public class BaselineLevelSelector {

    public BaselineLevel select(int validDayCount) {
        return BaselineLevel.fromValidDayCount(validDayCount);
    }
}
