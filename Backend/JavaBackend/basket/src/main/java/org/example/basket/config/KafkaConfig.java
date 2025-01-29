package org.example.basket.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.commondto.BasketProductEvent;
import org.example.commondto.ListBasketItemEvent;
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

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BasketProductEvent> kafkaListenerContainerFactory() {
        return KafkaConfigUtils.createKafkaListenerContainerFactory(
                KafkaConfigUtils.createJsonConsumerFactory(
                        KAFKA_BROKER,
                        "basket-service-group",
                        JsonDeserializer.class,
                        BasketProductEvent.class,
                        "org.example.common-dto"
                )
        );
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(KafkaConfigUtils.createProducerFactory(KAFKA_BROKER, org.apache.kafka.common.serialization.StringSerializer.class));
    }

    @Bean("BasketRemoveKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> BasketRemoveKafkaListenerContainerFactory() {
        return KafkaConfigUtils.createKafkaListenerContainerFactory(
                KafkaConfigUtils.createStringConsumerFactory(
                        KAFKA_BROKER,
                        "basket-remove-service-group"
                )
        );
    }

    @Bean("BasketItemsKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> BasketItemsKafkaListenerContainerFactory() {
        return KafkaConfigUtils.createKafkaListenerContainerFactory(
                KafkaConfigUtils.createStringConsumerFactory(
                        KAFKA_BROKER,
                        "basket-items-service-group"
                )
        );
    }

    @Bean
    public KafkaTemplate<String, ListBasketItemEvent> BasketItemsKafkaTemplate() {
        return new KafkaTemplate<>(KafkaConfigUtils.createProducerFactory(KAFKA_BROKER, JsonSerializer.class));
    }
}
