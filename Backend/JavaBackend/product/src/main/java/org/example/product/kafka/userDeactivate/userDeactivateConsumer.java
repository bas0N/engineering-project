package org.example.product.kafka.userDeactivate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.product.entity.Product;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class userDeactivateConsumer {
    private final MongoTemplate mongoTemplate;
    @KafkaListener(topics = "user-deactivate-request-topic", groupId = "user-service-basket-group", containerFactory = "userDeactivateKafkaListenerContainerFactory")
    public void consumeUserDeactivateEvent(String userId, Acknowledgment ack) {
        try {
            log.info("Consumed user deactivate event: {}", userId);
            if(userId == null) {
                throw new ResourceNotFoundException("User id is null");
            }
            Query query = new Query(Criteria.where("user_id").is(userId));
            Update update = new Update().set("isActive", false);
            mongoTemplate.updateMulti(query, update, Product.class);

        } catch (Exception e) {
            throw new RuntimeException("Exception kafka consumer", e);
        } finally {
            ack.acknowledge();
        }
    }
}
