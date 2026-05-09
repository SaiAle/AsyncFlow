package com.asyncflow.dto;

import com.asyncflow.entity.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateTaskRequest {
    @NotBlank
    private String title;
    private String description;
    /** Natural language scheduling input, e.g. "every Friday at 9 AM" */
    private String naturalLanguageInput;
    private TaskPriority priority = TaskPriority.MEDIUM;
    private Instant scheduledAt;
    private Instant dueAt;
    private int maxRetries = 3;
    private String assignedTo;
}
