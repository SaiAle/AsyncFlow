package com.asyncflow.controller;

import com.asyncflow.dto.DashboardStats;
import com.asyncflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TaskService taskService;

    @GetMapping("/stats")
    public Mono<DashboardStats> getStats(@RequestHeader("X-Tenant-ID") String tenantId) {
        return taskService.getDashboardStats(tenantId);
    }
}
