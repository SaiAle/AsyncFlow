package com.asyncflow.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardStats {
    private String tenantId;
    private long totalTasks;
    private long pendingTasks;
    private long inProgressTasks;
    private long completedTasks;
    private long failedTasks;
    private long retryPendingTasks;
    private Map<String, Long> tasksByPriority;
}
