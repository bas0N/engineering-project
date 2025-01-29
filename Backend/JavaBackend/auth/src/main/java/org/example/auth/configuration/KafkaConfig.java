package org.example.auth.configuration;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.commondto.UserDetailInfoEvent;
import org.example.commonkafkaconfig.common.KafkaConstants;
import org.example.commonkafkaconfig.config.KafkaConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class KafkaConfig {
    private static final String KAFKA_BROKER = KafkaConstants.DEFAULT_BOOTSTRAP_SERVERS;

    @Bean
    public KafkaTemplate<String, UserDetailInfoEvent> userKafkaTemplate() {
        return new KafkaTemplate<>(KafkaConfigUtils.createProducerFactory(KAFKA_BROKER, JsonSerializer.class));
    }

    @Bean(name = "userKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> userKafkaListenerContainerFactory() {
        DefaultKafkaConsumerFactory<String, String> consumerFactory = KafkaConfigUtils.createStringConsumerFactory(
                KAFKA_BROKER,
                "user-service-group"
        );
        return KafkaConfigUtils.createKafkaListenerContainerFactory(consumerFactory);
    }

    @Bean
    public KafkaTemplate<String, String> userDeactiveKafkaTemplate() {
        return new KafkaTemplate<>(KafkaConfigUtils.createProducerFactory(KAFKA_BROKER, org.apache.kafka.common.serialization.StringSerializer.class));
    }
}
