package org.example.product.kafka.basket;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.commondto.BasketProductEvent;
import org.example.product.entity.Image;
import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasketEventConsumer {
    private final ProductRepository productRepository;
    private final BasketEventProducer basketEventProducer;

    @KafkaListener(topics = "basket_product_request_topic", groupId = "product-service-basket-group")
    public void consumeBasketEvent(String productId) {
        Product product = productRepository.findByParentAsin(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        BasketProductEvent productEvent = new BasketProductEvent(
                product.getId(),
                product.getTitle(),
                Double.parseDouble(product.getPrice()),
                product.getImages().stream().map(Image::getThumb).toList()
        );
        basketEventProducer.sendBasketEvent(productEvent);
    }
}
