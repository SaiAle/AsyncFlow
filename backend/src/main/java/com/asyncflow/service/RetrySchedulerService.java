package com.asyncflow.service;

import com.asyncflow.ai.AITaskProcessor;
import com.asyncflow.entity.enums.TaskStatus;
import com.asyncflow.kafka.TaskEventMessage;
import com.asyncflow.kafka.TaskKafkaProducer;
import com.asyncflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetrySchedulerService {

    private final TaskRepository taskRepository;
    private final AITaskProcessor aiProcessor;
    private final TaskKafkaProducer kafkaProducer;

    @Scheduled(fixedDelayString = "${asyncflow.retry.check-interval-ms:30000}")
    public void processRetryQueue() {
        taskRepository.findTasksDueForRetry(Instant.now())
                .flatMap(task -> {
                    if (task.getRetryCount() >= task.getMaxRetries()) {
                        task.setStatus(TaskStatus.FAILED);
                        log.warn("Task {} exhausted retries, marking FAILED", task.getId());
                    } else {
                        task.setStatus(TaskStatus.IN_PROGRESS);
                        task.setRetryCount(task.getRetryCount() + 1);
                    }
                    return taskRepository.save(task)
                            .flatMap(saved -> kafkaProducer.publishRetryScheduled(
                                    TaskEventMessage.builder()
                                            .taskId(saved.getId())
                                            .tenantId(saved.getTenantId())
                                            .eventType("RETRY_TRIGGERED")
                                            .newStatus(saved.getStatus())
                                            .occurredAt(Instant.now())
                                            .build()
                            ));
                })
                .subscribe(
                        v -> log.debug("Retry processed"),
                        e -> log.error("Retry scheduler error: {}", e.getMessage())
                );
    }

    public Mono<Void> scheduleRetry(com.asyncflow.entity.Task task, String failureReason) {
        return aiProcessor.predictRetryDelay(task.getRetryCount(), task.getTitle(), failureReason)
                .flatMap(delaySeconds -> {
                    task.setStatus(TaskStatus.RETRY_PENDING);
                    task.setNextRetryAt(Instant.now().plusSeconds(delaySeconds));
                    return taskRepository.save(task).then();
                });
    }
}
