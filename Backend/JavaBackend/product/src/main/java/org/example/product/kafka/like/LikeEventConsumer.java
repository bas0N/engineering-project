package org.example.product.kafka.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.commondto.ImageEvent;
import org.example.commondto.LikeEvent;
import org.example.commondto.ProductEvent;
import org.example.product.entity.Product;
import org.example.product.mapper.ProductMapper;
import org.example.product.repository.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeEventConsumer {
    private final ProductRepository productRepository;
    private final LikeEventProducer likeEventProducer;

    @KafkaListener(
            topics = "like-events-topic",
            groupId = "product-service-group",
            containerFactory = "likeKafkaListenerContainerFactory"
    )
    public void handleLikeEvent(@Payload LikeEvent likeEvent, Acknowledgment ack) throws Exception {
        try {
            log.info("Consuming like event: {}", likeEvent);
            String productId = likeEvent.getProductId();
            Product product = productRepository.findByParentAsin(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
            likeEventProducer.sendProductEvent(ProductMapper.INSTANCE.toProductEvent(product));
            ack.acknowledge();
        } catch (ResourceNotFoundException e) {
            ack.acknowledge();
        } catch (Exception e) {
            throw new Exception("Error while handling like event", e);
        }
    }
}
