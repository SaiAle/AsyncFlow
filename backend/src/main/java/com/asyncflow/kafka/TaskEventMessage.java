package com.asyncflow.kafka;

import com.asyncflow.entity.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEventMessage {
    private UUID taskId;
    private String tenantId;
    private String eventType;
    private TaskStatus previousStatus;
    private TaskStatus newStatus;
    private String triggeredBy;
    private Instant occurredAt;
    private String payload;
}
