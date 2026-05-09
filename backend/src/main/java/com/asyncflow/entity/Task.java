package com.asyncflow.entity;

import com.asyncflow.entity.enums.TaskPriority;
import com.asyncflow.entity.enums.TaskStatus;
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
@Table("tasks")
public class Task {

    @Id
    private UUID id;

    @Column("tenant_id")
    private String tenantId;

    private String title;
    private String description;

    @Column("natural_language_input")
    private String naturalLanguageInput;

    private TaskStatus status;
    private TaskPriority priority;

    @Column("scheduled_at")
    private Instant scheduledAt;

    @Column("due_at")
    private Instant dueAt;

    @Column("retry_count")
    private int retryCount;

    @Column("max_retries")
    private int maxRetries;

    @Column("next_retry_at")
    private Instant nextRetryAt;

    @Column("ai_context")
    private String aiContext;

    @Column("assigned_to")
    private String assignedTo;

    @Column("created_by")
    private String createdBy;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    @Version
    private Long version;
}
