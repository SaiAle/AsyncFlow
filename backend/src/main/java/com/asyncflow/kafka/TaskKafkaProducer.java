package com.asyncflow.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskKafkaProducer {

    private final KafkaTemplate<String, TaskEventMessage> kafkaTemplate;

    @Value("${asyncflow.kafka.topics.task-created}") private String taskCreatedTopic;
    @Value("${asyncflow.kafka.topics.task-updated}") private String taskUpdatedTopic;
    @Value("${asyncflow.kafka.topics.task-completed}") private String taskCompletedTopic;
    @Value("${asyncflow.kafka.topics.retry-scheduled}") private String retryTopic;

    public Mono<Void> publishTaskCreated(TaskEventMessage event) {
        return publish(taskCreatedTopic, event.getTaskId(), event);
    }

    public Mono<Void> publishTaskUpdated(TaskEventMessage event) {
        return publish(taskUpdatedTopic, event.getTaskId(), event);
    }

    public Mono<Void> publishTaskCompleted(TaskEventMessage event) {
        return publish(taskCompletedTopic, event.getTaskId(), event);
    }

    public Mono<Void> publishRetryScheduled(TaskEventMessage event) {
        return publish(retryTopic, event.getTaskId(), event);
    }

    private Mono<Void> publish(String topic, UUID key, TaskEventMessage msg) {
        return Mono.fromCallable(() -> kafkaTemplate.send(topic, key.toString(), msg))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(r -> log.debug("Published to {}: taskId={}", topic, key))
                .doOnError(e -> log.error("Failed to publish to {}: {}", topic, e.getMessage()))
                .then();
    }
}
