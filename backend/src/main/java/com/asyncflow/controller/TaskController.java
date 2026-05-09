package com.asyncflow.controller;

import com.asyncflow.dto.CreateTaskRequest;
import com.asyncflow.dto.TaskResponse;
import com.asyncflow.entity.enums.TaskStatus;
import com.asyncflow.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        return taskService.createTask(request, tenantId, userId);
    }

    @GetMapping
    public Flux<TaskResponse> listTasks(@RequestHeader("X-Tenant-ID") String tenantId) {
        return taskService.getTasksByTenant(tenantId);
    }

    @GetMapping("/{id}")
    public Mono<TaskResponse> getTask(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return taskService.getTaskById(id, tenantId);
    }

    @PatchMapping("/{id}/status")
    public Mono<TaskResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam TaskStatus status,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        return taskService.updateTaskStatus(id, status, tenantId, userId);
    }
}
