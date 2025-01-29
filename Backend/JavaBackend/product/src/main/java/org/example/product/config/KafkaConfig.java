package org.example.product.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.commondto.*;
import org.example.commonkafkaconfig.common.KafkaConstants;
import org.example.commonkafkaconfig.config.KafkaConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    private static final String KAFKA_BROKER = KafkaConstants.DEFAULT_BOOTSTRAP_SERVERS;

    //LIKE
    @Bean(name = "likeKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, LikeEvent> likeKafkaListenerContainerFactory() {
        return KafkaConfigUtils.createKafkaListenerContainerFactory(
                KafkaConfigUtils.createJsonConsumerFactory(
                        KAFKA_BROKER,
                        "product-service-group",
                        JsonDeserializer.class,
                        LikeEvent.class,
                        "org.example.common-dto"
                )
        );
    }

    @Bean
    public KafkaTemplate<String, ProductEvent> productKafkaTemplate() {
        return new KafkaTemplate<>(KafkaConfigUtils.createProducerFactory(KAFKA_BROKER, JsonSerializer.class));
    }

    //BASKET

    @Bean
    public KafkaTemplate<String, BasketProductEvent> basketKafkaTemplate() {
        return new KafkaTemplate<>(KafkaConfigUtils.createProducerFactory(KAFKA_BROKER, JsonSerializer.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> basketKafkaListenerContainerFactory() {
        return KafkaConfigUtils.createKafkaListenerContainerFactory(
                KafkaConfigUtils.createStringConsumerFactory(
                        KAFKA_BROKER,
                        "product-service-basket-group"
                )
        );
    }

    //USER

    @Bean
    public KafkaTemplate<String, String> userKafkaTemplate() {
        return new KafkaTemplate<>(KafkaConfigUtils.createProducerFactory(KAFKA_BROKER, StringSerializer.class));
    }

    @Bean(name = "userKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UserDetailInfoEvent> userKafkaListenerContainerFactory() {
        return KafkaConfigUtils.createKafkaListenerContainerFactory(
                KafkaConfigUtils.createJsonConsumerFactory(
                        KAFKA_BROKER,
                        "product-service-user-group",
                        JsonDeserializer.class,
                        UserDetailInfoEvent.class,
                        "org.example.common-dto"
                )
        );
    }

    //User Deactivate
    @Bean(name = "userDeactivateKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> userDeactivateKafkaListenerContainerFactory() {
        return KafkaConfigUtils.createKafkaListenerContainerFactory(
                KafkaConfigUtils.createStringConsumerFactory(
                        KAFKA_BROKER,
                        "product-service-userdeactivate-group"
                )
        );
    }
}
