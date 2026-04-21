package com.cpc.sleepcodex.baseline.service;

import com.cpc.sleepcodex.baseline.model.NightIsolationResult;
import com.cpc.sleepcodex.baseline.model.SegmentIsolationResult;

import java.util.List;

public class NightIsolationService {

    public NightIsolationResult isolate(List<SegmentIsolationResult> segmentResults) {
        int total = segmentResults.size();
        int accepted = (int) segmentResults.stream().filter(SegmentIsolationResult::accepted).count();

        if (total == 0) {
            return new NightIsolationResult(false, "无可用片段", 0, 0);
        }
        if (accepted < 6) {
            return new NightIsolationResult(false, "稳定片段数量不足", accepted, total);
        }
        double ratio = accepted * 1.0 / total;
        if (ratio < 0.6) {
            return new NightIsolationResult(false, "夜级异常比例过高", accepted, total);
        }
        return new NightIsolationResult(true, "通过夜级筛选", accepted, total);
    }
}
