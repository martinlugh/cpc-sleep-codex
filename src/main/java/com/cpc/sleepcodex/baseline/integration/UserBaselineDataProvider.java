package com.cpc.sleepcodex.baseline.integration;

import com.cpc.sleepcodex.baseline.model.UserBaselineSnapshot;

public interface UserBaselineDataProvider {
    UserBaselineSnapshot load(String userId);
}
