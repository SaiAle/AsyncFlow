package com.asyncflow.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${asyncflow.kafka.topics.task-created}") private String taskCreated;
    @Value("${asyncflow.kafka.topics.task-updated}") private String taskUpdated;
    @Value("${asyncflow.kafka.topics.task-completed}") private String taskCompleted;
    @Value("${asyncflow.kafka.topics.retry-scheduled}") private String retryScheduled;

    @Bean public NewTopic taskCreatedTopic() {
        return TopicBuilder.name(taskCreated).partitions(6).replicas(1).build();
    }
    @Bean public NewTopic taskUpdatedTopic() {
        return TopicBuilder.name(taskUpdated).partitions(6).replicas(1).build();
    }
    @Bean public NewTopic taskCompletedTopic() {
        return TopicBuilder.name(taskCompleted).partitions(6).replicas(1).build();
    }
    @Bean public NewTopic retryScheduledTopic() {
        return TopicBuilder.name(retryScheduled).partitions(3).replicas(1).build();
    }
}
