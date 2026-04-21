package com.cpc.sleepcodex.decision.rule;

public final class RuleThresholds {
    private RuleThresholds() {
    }

    public static final double STEP_WAKE_THRESHOLD = 0.1;
    public static final double STEP_QUIET_THRESHOLD = 0.1;

    public static final double HFC_HIGH = 0.65;
    public static final double HFC_LOW = 0.35;

    public static final double LFC_LOW = 0.35;
    public static final double VLFC_LOW = 0.30;
    public static final double VLFC_HIGH = 0.70;

    public static final double COUPLING_LOW = 0.90;
    public static final double COUPLING_MODERATE_LOW = 0.90;
    public static final double COUPLING_MODERATE_HIGH = 1.30;
    public static final double COUPLING_HIGH = 1.35;

    public static final double ENTROPY_LOW = 0.45;
    public static final double ENTROPY_MID = 0.60;
    public static final double ENTROPY_HIGH = 0.75;

    public static final double HEART_RATE_HIGH = 72.0;
    public static final double HEART_RATE_LOW = 58.0;
    public static final double RMSSD_HIGH = 40.0;
    public static final double RMSSD_LOW = 25.0;
    public static final double RESP_WAKE_HIGH = 16.0;

    public static final double RESP_STABLE_STD = 0.90;
    public static final double RESP_UNSTABLE_STD = 1.60;

    public static final double HEART_VAR_DEEP_MAX = 3.0;
    public static final double HEART_VAR_REM_MIN = 4.0;
}
