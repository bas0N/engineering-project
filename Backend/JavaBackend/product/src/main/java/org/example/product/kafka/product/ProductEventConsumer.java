package org.example.product.kafka.product;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.commondto.ImageEvent;
import org.example.commondto.LikeEvent;
import org.example.commondto.ProductEvent;
import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductEventConsumer {

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;
    private final ProductRepository productRepository;

    public ProductEventConsumer(KafkaTemplate<String, ProductEvent> kafkaTemplate, ProductRepository productRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.productRepository = productRepository;
    }

    @KafkaListener(
            topics = "like-events",
            groupId = "product-service-group",
            containerFactory = "productKafkaListenerContainerFactory"
    )
    public void handleLikeEvent(LikeEvent likeEvent, Acknowledgment ack) throws Exception {
        try {
            String productId = likeEvent.getProductId();
            Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

            List<ImageEvent> imageEventList = product.getImages().stream().map(image -> new ImageEvent(image.getThumb(), image.getLarge(), image.getLarge(), image.getHiRes())).toList();
            ProductEvent productEvent = new ProductEvent(product.getId(), product.getTitle(), product.getPrice(), product.getRatingNumber(), product.getAverageRating(), imageEventList);
            kafkaTemplate.send("product-details", productEvent);
            ack.acknowledge();
        } catch (ResourceNotFoundException e) {
            ack.acknowledge();
        } catch (Exception e) {
            throw new Exception("Error while handling like event", e);
        }
    }
}
