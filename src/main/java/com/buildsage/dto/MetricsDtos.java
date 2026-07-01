package com.buildsage.dto;

public final class MetricsDtos {
    private MetricsDtos() {}

    public record ProjectMetrics(
            long totalRuns,
            long runsLast7Days,
            long failedRuns,
            long failedRunsLast7Days,
            double successRate,
            long openIncidents,
            long totalDeployments,
            long highRiskDeployments,
            double deploymentRiskAverage) {}
}
