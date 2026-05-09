package com.asyncflow.dto;

import com.asyncflow.entity.enums.TaskPriority;
import com.asyncflow.entity.enums.TaskStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TaskResponse {
    private UUID id;
    private String tenantId;
    private String title;
    private String description;
    private String naturalLanguageInput;
    private TaskStatus status;
    private TaskPriority priority;
    private Instant scheduledAt;
    private Instant dueAt;
    private int retryCount;
    private int maxRetries;
    private Instant nextRetryAt;
    private String assignedTo;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}
