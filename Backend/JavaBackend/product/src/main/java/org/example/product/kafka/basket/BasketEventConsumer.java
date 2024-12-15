package org.example.product.kafka.basket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.commondto.BasketProductEvent;
import org.example.product.entity.Image;
import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketEventConsumer {
    private final ProductRepository productRepository;
    private final BasketEventProducer basketEventProducer;

    @KafkaListener(topics = "basket-product-request-topic", groupId = "product-service-basket-group", containerFactory = "basketKafkaListenerContainerFactory")
    public void consumeBasketEvent(String productId, Acknowledgment ack) {
        try {
            log.info("Consumed basket event: {}", productId);
            if(productId == null) {
                throw new ResourceNotFoundException("Product id is null");
            }
            Product product = productRepository.findByParentAsin(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

            BasketProductEvent productEvent = new BasketProductEvent(
                    product.getParentAsin(),
                    product.getTitle()==null ? "" : product.getTitle(),
                    product.getPrice()==null ? 0.0 : product.getPrice(),
                    product.getImages()==null ? null : product.getImages().stream().map(Image::getThumb).toList(),
                    product.getIsActive()
            );
            basketEventProducer.sendBasketEvent(productEvent);
        } catch (Exception e) {
            throw new RuntimeException("Exception kafka consumer", e);
        } finally {
            ack.acknowledge();
        }
    }

}
