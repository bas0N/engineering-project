package org.example.order.config;

import org.apache.kafka.common.serialization.StringSerializer;
import org.example.commondto.ListBasketItemEvent;
import org.example.commondto.UserDetailInfoEvent;
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
    //USER
    private static final String KAFKA_BROKER = "kafka:9092";

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDetailInfoEvent> userKafkaListenerContainerFactory() {
        return KafkaConfigUtils.createKafkaListenerContainerFactory(
                KafkaConfigUtils.createJsonConsumerFactory(
                        KAFKA_BROKER,
                        "order-service-user-group",
                        JsonDeserializer.class,
                        UserDetailInfoEvent.class,
                        "org.example.common-dto"
                )
        );
    }

    @Bean
    public KafkaTemplate<String, String> userKafkaTemplate() {
        return new KafkaTemplate<>(KafkaConfigUtils.createProducerFactory(KAFKA_BROKER, StringSerializer.class));
    }


    ///BasketItemsKafkaProducer
    @Bean
    public KafkaTemplate<String, String> basketItemsKafkaTemplate() {
        return new KafkaTemplate<>(KafkaConfigUtils.createProducerFactory(KAFKA_BROKER, StringSerializer.class));
    }

    @Bean(name = "basketItemsKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ListBasketItemEvent> basketItemsKafkaListenerContainerFactory() {
        return KafkaConfigUtils.createKafkaListenerContainerFactory(
                KafkaConfigUtils.createJsonConsumerFactory(
                        KAFKA_BROKER,
                        "order-service-basketItems-group",
                        JsonDeserializer.class,
                        ListBasketItemEvent.class,
                        "org.example.common-dto"
                )
        );
    }

    //BASKET REMOVE
    @Bean
    public KafkaTemplate<String, String> basketRemoveKafkaTemplate() {
        return new KafkaTemplate<>(KafkaConfigUtils.createProducerFactory(KAFKA_BROKER, JsonSerializer.class));
    }
}
