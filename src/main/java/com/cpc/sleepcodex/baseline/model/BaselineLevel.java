package com.cpc.sleepcodex.baseline.model;

public enum BaselineLevel {
    GENERIC,
    DAY_3,
    DAY_7,
    DAY_21;

    public static BaselineLevel fromValidDayCount(int validDayCount) {
        if (validDayCount >= 21) {
            return DAY_21;
        }
        if (validDayCount >= 7) {
            return DAY_7;
        }
        if (validDayCount >= 3) {
            return DAY_3;
        }
        return GENERIC;
    }
}
