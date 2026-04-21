package com.cpc.sleepcodex.decision.rule;

public final class BaselineThresholdContextHolder {

    private static final ThreadLocal<BaselineThresholdContext> CONTEXT = new ThreadLocal<>();

    private BaselineThresholdContextHolder() {
    }

    public static void set(BaselineThresholdContext context) {
        CONTEXT.set(context);
    }

    public static BaselineThresholdContext get() {
        BaselineThresholdContext context = CONTEXT.get();
        return context == null ? BaselineThresholdContext.generic() : context;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
