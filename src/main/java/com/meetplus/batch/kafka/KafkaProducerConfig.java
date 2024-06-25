package com.meetplus.batch.kafka;

import com.meetplus.batch.kafka.Topics.Constant;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public NewTopic myTopic() {
        return TopicBuilder.name(Constant.ALARM_TOPIC)
            .partitions(1)
            .replicas(1)
            .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(86400000)) // 1일 (24시간) = 86400000 밀리초
            .build();
    }

    @Bean
    public NewTopic myTopic2() {
        return TopicBuilder.name(Topics.Constant.AUCTION_POST_DONATION_UPDATE)
            .partitions(1)
            .replicas(1)
            .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(86400000)) // 1일 (24시간) = 86400000 밀리초
            .build();
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
