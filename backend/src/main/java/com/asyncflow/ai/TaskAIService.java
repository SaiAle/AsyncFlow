package com.asyncflow.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import reactor.core.publisher.Mono;

public interface TaskAIService {

    @SystemMessage("""
        You are an intelligent task scheduler assistant.
        Parse the user's natural language input and extract:
        - A clean task title (concise, action-oriented)
        - ISO 8601 scheduled time (if mentioned)
        - Priority level: LOW, MEDIUM, HIGH, or CRITICAL
        - Recurrence pattern (if any): ONCE, DAILY, WEEKLY, MONTHLY
        Respond ONLY with valid JSON. No explanation.
        """)
    @UserMessage("Parse this task request: {{input}}")
    String parseTaskInput(@V("input") String input);

    @SystemMessage("""
        You are a retry timing optimizer.
        Based on historical failure patterns, suggest the best retry delay in seconds.
        Consider: time of day, failure reason, retry count.
        Respond with a single integer (seconds to wait before retry). No explanation.
        """)
    @UserMessage("Retry attempt {{retryCount}} for task type '{{taskType}}'. Last failure: {{failureReason}}. Suggest delay in seconds.")
    String suggestRetryDelay(@V"retryCount") int retryCount,
                             @V("taskType") String taskType,
                             @V("failureReason") String failureReason);
}
