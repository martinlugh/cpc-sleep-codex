package com.cpc.sleepcodex.baseline.service;

import com.cpc.sleepcodex.baseline.model.BaselineMetricType;
import com.cpc.sleepcodex.baseline.model.BaselineSegment;
import com.cpc.sleepcodex.baseline.model.SegmentIsolationResult;

public class SegmentIsolationService {

    public SegmentIsolationResult isolate(BaselineSegment segment) {
        if ("WAKE".equals(segment.sleepStage())) {
            return new SegmentIsolationResult(false, "清醒片段不参与基线");
        }
        if (segment.confidence() < 0.55) {
            return new SegmentIsolationResult(false, "置信度过低");
        }

        double heartRate = segment.metrics().getOrDefault(BaselineMetricType.HEART_RATE, 0.0);
        double respirationRate = segment.metrics().getOrDefault(BaselineMetricType.RESPIRATION_RATE, 0.0);
        double entropy = segment.metrics().getOrDefault(BaselineMetricType.SAMPLE_ENTROPY, 0.0);

        if (heartRate < 30 || heartRate > 120) {
            return new SegmentIsolationResult(false, "心率异常");
        }
        if (respirationRate < 5 || respirationRate > 30) {
            return new SegmentIsolationResult(false, "呼吸率异常");
        }
        if (entropy < 0.05 || entropy > 2.5) {
            return new SegmentIsolationResult(false, "熵值异常");
        }

        return new SegmentIsolationResult(true, "通过段级筛选");
    }
}
