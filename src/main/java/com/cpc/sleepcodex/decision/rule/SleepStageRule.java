package com.cpc.sleepcodex.decision.rule;

import com.cpc.sleepcodex.decision.domain.AlignedSegmentContext;

import java.util.List;

public interface SleepStageRule {
    List<RuleHit> evaluate(AlignedSegmentContext context);
}
