package com.asyncflow.repository;

import com.asyncflow.entity.TaskEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TaskEventRepository extends ReactiveCrudRepository<TaskEvent, UUID> {
    Flux<TaskEvent> findAllByTaskIdOrderByOccurredAtAsc(UUID taskId);
    Flux<TaskEvent> findAllByTenantId(String tenantId);
}
