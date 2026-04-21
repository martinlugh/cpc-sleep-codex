package com.cpc.sleepcodex.baseline.service;

import com.cpc.sleepcodex.baseline.model.RobustMetricStats;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RobustStatisticsService {

    public RobustMetricStats compute(List<Double> rawValues) {
        if (rawValues.isEmpty()) {
            return new RobustMetricStats(0.0, 0.0, 0.0, 0);
        }
        List<Double> values = new ArrayList<>(rawValues);
        values.sort(Comparator.naturalOrder());
        double median = percentile(values, 0.5);
        double q1 = percentile(values, 0.25);
        double q3 = percentile(values, 0.75);
        double iqr = q3 - q1;

        List<Double> deviations = values.stream().map(v -> Math.abs(v - median)).sorted().toList();
        double mad = percentile(deviations, 0.5);

        return new RobustMetricStats(median, iqr, mad, values.size());
    }

    private double percentile(List<Double> sortedValues, double p) {
        if (sortedValues.size() == 1) {
            return sortedValues.get(0);
        }
        double index = p * (sortedValues.size() - 1);
        int low = (int) Math.floor(index);
        int high = (int) Math.ceil(index);
        if (low == high) {
            return sortedValues.get(low);
        }
        double ratio = index - low;
        return sortedValues.get(low) * (1 - ratio) + sortedValues.get(high) * ratio;
    }
}
