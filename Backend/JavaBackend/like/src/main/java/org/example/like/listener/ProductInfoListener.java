package org.example.like.listener;

import org.example.commondto.ProductEvent;
import org.example.like.entity.Image;
import org.example.like.entity.Product;
import org.example.like.repository.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductInfoListener {
    private final ProductRepository productRepository;

    public ProductInfoListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @KafkaListener(
            topics = "product-details",
            groupId = "like-service-group",
            containerFactory = "likeKafkaListenerContainerFactory"
    )
    public void handleProductDetails(ProductEvent productEvent, Acknowledgment ack) {
        if(productRepository.findByUuid(productEvent.getProductId()).isPresent()) {
            ack.acknowledge();
            return;
        }

        Product product = new Product(
                productEvent.getProductId(),
                productEvent.getTitle(),
                productEvent.getPrice(),
                productEvent.getRatingNumber(),
                productEvent.getAverageRating(),
                new ArrayList<>()
        );

        List<Image> images = new ArrayList<>();
        productEvent.getImages().forEach(imageEvent -> {
            Image image = new Image(
                    imageEvent.getThumb(),
                    imageEvent.getLarge(),
                    imageEvent.getVariant(),
                    imageEvent.getHiRes(),
                    product
            );
            images.add(image);
        });
        product.setImages(images);
        productRepository.save(product);
        ack.acknowledge();
    }
}
