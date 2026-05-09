package com.asyncflow.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("task_events")
public class TaskEvent {

    @Id
    private UUID id;

    @Column("task_id")
    private UUID taskId;

    @Column("tenant_id")
    private String tenantId;

    @Column("event_type")
    private String eventType;

    private String payload;

    @Column("created_by")
    private String createdBy;

    @CreatedDate
    @Column("occurred_at")
    private Instant occurredAt;
}
