package org.example.like.listener;

import org.example.commondto.ProductEvent;
import org.example.like.entity.Image;
import org.example.like.entity.Product;
import org.example.like.repository.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
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

    @KafkaListener(topics = "product-details", groupId = "like-service-group")
    public void handleProductDetails(ProductEvent productEvent) {
        Optional<Product> optionalProduct = productRepository.findByUuid(productEvent.getProductId());
        Product product = optionalProduct.orElseGet(Product::new);
        product.setUuid(productEvent.getProductId());
        product.setTitle(productEvent.getTitle());
        product.setPrice(productEvent.getPrice());
        product.setRatingNumber(productEvent.getRatingNumber());
        product.setAverageRating(productEvent.getAverageRating());

        List<Image> images = new ArrayList<>();
        productEvent.getImages().forEach(imageEvent -> {
            Image image = new Image();
            image.setThumb(imageEvent.getThumb());
            image.setLarge(imageEvent.getLarge());
            image.setVariant(imageEvent.getVariant());
            image.setHiRes(imageEvent.getHiRes());
            image.setProduct(product);
            images.add(image);
        });

        product.setImages(images);
        productRepository.save(product);
    }
}
