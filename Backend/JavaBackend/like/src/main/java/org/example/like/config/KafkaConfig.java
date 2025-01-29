package org.example.like.config;

import org.example.commondto.LikeEvent;
import org.example.commondto.ProductEvent;
import org.example.commonkafkaconfig.common.KafkaConstants;
import org.example.commonkafkaconfig.config.KafkaConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class KafkaConfig {
    private static final String KAFKA_BROKER = KafkaConstants.DEFAULT_BOOTSTRAP_SERVERS;

    @Bean(name = "likeKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ProductEvent> kafkaListenerContainerFactory() {
        return KafkaConfigUtils.createKafkaListenerContainerFactory(
                KafkaConfigUtils.createJsonConsumerFactory(
                        KAFKA_BROKER,
                        "like-service-group",
                        JsonDeserializer.class,
                        ProductEvent.class,
                        "org.example.common-dto"
                )
        );
    }

    @Bean
    public KafkaTemplate<String, LikeEvent> kafkaTemplate() {
        return new KafkaTemplate<>(KafkaConfigUtils.createProducerFactory(KAFKA_BROKER, JsonSerializer.class));
    }
}
