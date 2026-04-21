package com.cpc.sleepcodex.baseline.integration;

import com.cpc.sleepcodex.baseline.model.BaselineLevel;
import com.cpc.sleepcodex.baseline.model.UserBaselineSnapshot;

import java.util.Map;

public class NoopUserBaselineDataProvider implements UserBaselineDataProvider {

    @Override
    public UserBaselineSnapshot load(String userId) {
        return new UserBaselineSnapshot(userId, BaselineLevel.GENERIC, 0, Map.of());
    }
}
