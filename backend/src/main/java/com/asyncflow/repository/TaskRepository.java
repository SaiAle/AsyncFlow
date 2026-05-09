package com.asyncflow.repository;

import com.asyncflow.entity.Task;
import com.asyncflow.entity.enums.TaskStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface TaskRepository extends ReactiveCrudRepository<Task, UUID> {

    Flux<Task> findAllByTenantId(String tenantId);

    Flux<Task> findAllByTenantIdAndStatus(String tenantId, TaskStatus status);

    @Query("SELECT * FROM tasks WHERE tenant_id = :tenantId AND status = :status ORDER BY priority DESC, scheduled_at ASC LIMIT :limit")
    Flux<Task> findTopByTenantIdAndStatus(String tenantId, String status, int limit);

    @Query("SELECT * FROM tasks WHERE status = 'RETRY_PENDING' AND next_retry_at <= :now")
    Flux<Task> findTasksDueForRetry(Instant now);

    @Query("SELECT COUNT(*) FROM tasks WHERE tenant_id = :tenantId AND status = :status")
    Mono<Long> countByTenantIdAndStatus(String tenantId, String status);
}
