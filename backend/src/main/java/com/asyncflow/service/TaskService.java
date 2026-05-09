package com.asyncflow.service;

import com.asyncflow.ai.AITaskProcessor;
import com.asyncflow.dto.CreateTaskRequest;
import com.asyncflow.dto.DashboardStats;
import com.asyncflow.dto.TaskResponse;
import com.asyncflow.entity.Task;
import com.asyncflow.entity.TaskEvent;
import com.asyncflow.entity.enums.TaskStatus;
import com.asyncflow.kafka.TaskEventMessage;
import com.asyncflow.kafka.TaskKafkaProducer;
import com.asyncflow.repository.TaskEventRepository;
import com.asyncflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventRepository taskEventRepository;
    private final AITaskProcessor aiProcessor;
    private final TaskKafkaProducer kafkaProducer;

    @Transactional
    public Mono<TaskResponse> createTask(CreateTaskRequest request, String tenantId, String userId) {
        return aiProcessor.enrichTaskRequest(request)
                .flatMap(enriched -> {
                    Task task = Task.builder()
                            .id(UUID.randomUUID())
                            .tenantId(tenantId)
                            .title(enriched.getTitle())
                            .description(enriched.getDescription())
                            .naturalLanguageInput(enriched.getNaturalLanguageInput())
                            .status(TaskStatus.PENDING)
                            .priority(enriched.getPriority())
                            .scheduledAt(enriched.getScheduledAt())
                            .dueAt(enriched.getDueAt())
                            .maxRetries(enriched.getMaxRetries())
                            .retryCount(0)
                            .assignedTo(enriched.getAssignedTo())
                            .createdBy(userId)
                            .build();
                    return taskRepository.save(task);
                })
                .flatMap(saved -> {
                    TaskEvent event = TaskEvent.builder()
                            .id(UUID.randomUUID())
                            .taskId(saved.getId())
                            .tenantId(tenantId)
                            .eventType("TASK_CREATED")
                            .payload("{\"status\":\"PENDING\"}")
                            .createdBy(userId)
                            .build();
                    return taskEventRepository.save(event).thenReturn(saved);
                })
                .flatMap(saved -> kafkaProducer.publishTaskCreated(
                        TaskEventMessage.builder()
                                .taskId(saved.getId())
                                .tenantId(tenantId)
                                .eventType("TASK_CREATED")
                                .newStatus(TaskStatus.PENDING)
                                .triggeredBy(userId)
                                .occurredAt(Instant.now())
                                .build()
                ).thenReturn(saved))
                .map(this::toResponse)
                .doOnSuccess(t -> log.info("Task created: id={} tenant={}", t.getId(), tenantId));
    }

    public Flux<TaskResponse> getTasksByTenant(String tenantId) {
        return taskRepository.findAllByTenantId(tenantId).map(this::toResponse);
    }

    public Mono<TaskResponse> getTaskById(UUID id, String tenantId) {
        return taskRepository.findById(id)
                .filter(t -> t.getTenantId().equals(tenantId))
                .map(this::toResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Task not found: " + id)));
    }

    @Transactional
    public Mono<TaskResponse> updateTaskStatus(UUID id, TaskStatus newStatus, String tenantId, String userId) {
        return taskRepository.findById(id)
                .filter(t -> t.getTenantId().equals(tenantId))
                .switchIfEmpty(Mono.error(new RuntimeException("Task not found: " + id)))
                .flatMap(task -> {
                    TaskStatus prev = task.getStatus();
                    task.setStatus(newStatus);
                    return taskRepository.save(task)
                            .flatMap(saved -> {
                                TaskEvent ev = TaskEvent.builder()
                                        .id(UUID.randomUUID())
                                        .taskId(saved.getId())
                                        .tenantId(tenantId)
                                        .eventType("STATUS_CHANGED")
                                        .payload("{\"from\":\"" + prev + "\",\"to\":\"" + newStatus + "\"}")
                                        .createdBy(userId)
                                        .build();
                                return taskEventRepository.save(ev)
                                        .then(kafkaProducer.publishTaskUpdated(
                                                TaskEventMessage.builder()
                                                        .taskId(saved.getId())
                                                        .tenantId(tenantId)
                                                        .eventType("STATUS_CHANGED")
                                                        .previousStatus(prev)
                                                        .newStatus(newStatus)
                                                        .triggeredBy(userId)
                                                        .occurredAt(Instant.now())
                                                        .build()))
                                        .thenReturn(saved);
                            });
                })
                .map(this::toResponse);
    }

    public Mono<DashboardStats> getDashboardStats(String tenantId) {
        return Mono.zip(
                taskRepository.countByTenantIdAndStatus(tenantId, "PENDING"),
                taskRepository.countByTenantIdAndStatus(tenantId, "IN_PROGRESS"),
                taskRepository.countByTenantIdAndStatus(tenantId, "COMPLETED"),
                taskRepository.countByTenantIdAndStatus(tenantId, "FAILED"),
                taskRepository.countByTenantIdAndStatus(tenantId, "RETRY_PENDING")
        ).map(t -> DashboardStats.builder()
                .tenantId(tenantId)
                .pendingTasks(t.getT1())
                .inProgressTasks(t.getT2())
                .completedTasks(t.getT3())
                .failedTasks(t.getT4())
                .retryPendingTasks(t.getT5())
                .totalTasks(t.getT1() + t.getT2() + t.getT3() + t.getT4() + t.getT5())
                .build());
    }

    private TaskResponse toResponse(Task t) {
        TaskResponse r = new TaskResponse();
        r.setId(t.getId());
        r.setTenantId(t.getTenantId());
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());
        r.setNaturalLanguageInput(t.getNaturalLanguageInput());
        r.setStatus(t.getStatus());
        r.setPriority(t.getPriority());
        r.setScheduledAt(t.getScheduledAt());
        r.setDueAt(t.getDueAt());
        r.setRetryCount(t.getRetryCount());
        r.setMaxRetries(t.getMaxRetries());
        r.setNextRetryAt(t.getNextRetryAt());
        r.setAssignedTo(t.getAssignedTo());
        r.setCreatedBy(t.getCreatedBy());
        r.setCreatedAt(t.getCreatedAt());
        r.setUpdatedAt(t.getUpdatedAt());
        return r;
    }
}
