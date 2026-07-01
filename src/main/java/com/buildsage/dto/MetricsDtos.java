package com.buildsage.dto;

public final class MetricsDtos {
    private MetricsDtos() {}

    public record ProjectMetrics(
            long runsLast7Days, long failedRuns, long openIncidents, double deploymentRiskAverage) {}
}
