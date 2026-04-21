package com.cpc.sleepcodex.baseline.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class UserBaselineProfile {

    private final String userId;
    private final Deque<ValidBaselineDay> validDays = new ArrayDeque<>();

    public UserBaselineProfile(String userId) {
        this.userId = userId;
    }

    public String userId() {
        return userId;
    }

    public void addValidDay(ValidBaselineDay day) {
        validDays.addLast(day);
        while (validDays.size() > 21) {
            validDays.removeFirst();
        }
    }

    public int validDayCount() {
        return validDays.size();
    }

    public List<ValidBaselineDay> validDaysSnapshot() {
        return new ArrayList<>(validDays);
    }
}
