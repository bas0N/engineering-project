package org.example.like.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commondto.LikeEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductInfoProducer {
    private static final String PRODUCT_TOPIC = "like-events-topic";

    private final KafkaTemplate<String, LikeEvent> userKafkaTemplate;

    public void sendProductEvent(LikeEvent likeEvent) {
        log.info("Producing user event: {}", likeEvent);
        userKafkaTemplate.send(PRODUCT_TOPIC, likeEvent);
    }
}
