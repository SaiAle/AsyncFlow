package com.asyncflow.ai;

import com.asyncflow.dto.CreateTaskRequest;
import com.asyncflow.entity.enums.TaskPriority;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AITaskProcessor {

    private final TaskAIService aiService;
    private final ObjectMapper objectMapper;

    @CircuitBreaker(name = "ai-processor", fallbackMethod = "parseTaskFallback")
    @Retry(name = "ai-processor")
    public Mono<CreateTaskRequest> enrichTaskRequest(CreateTaskRequest request) {
        if (request.getNaturalLanguageInput() == null || request.getNaturalLanguageInput().isBlank()) {
            return Mono.just(request);
        }

        return Mono.fromCallable(() -> {
            String raw = aiService.parseTaskInput(request.getNaturalLanguageInput());
            JsonNode node = objectMapper.readTree(raw);

            if (node.has("title") && (request.getTitle() == null || request.getTitle().isBlank())) {
                request.setTitle(node.get("title").asText());
            }
            if (node.has("scheduledAt") && request.getScheduledAt() == null) {
                request.setScheduledAt(Instant.parse(node.get("scheduledAt").asText()));
            }
            if (node.has("priority")) {
                try {
                    request.setPriority(TaskPriority.valueOf(node.get("priority").asText().toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }

            log.debug("AI enriched task: title={}, priority={}", request.getTitle(), request.getPriority());
            return request;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Long> predictRetryDelay(int retryCount, String taskType, String failureReason) {
        return Mono.fromCallable(() -> {
            String raw = aiService.suggestRetryDelay(retryCount, taskType, failureReason);
            return Long.parseLong(raw.trim());
        })
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorReturn(60L); // fallback: 60s
    }

    // Circuit breaker fallback
    private Mono<CreateTaskRequest> parseTaskFallback(CreateTaskRequest request, Throwable t) {
        log.warn("AI circuit open, using raw request. Cause: {}", t.getMessage());
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            request.setTitle(request.getNaturalLanguageInput());
        }
        return Mono.just(request);
    }
}
