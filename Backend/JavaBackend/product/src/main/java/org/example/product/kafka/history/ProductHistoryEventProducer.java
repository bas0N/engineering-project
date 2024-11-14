package org.example.product.kafka.history;

import lombok.RequiredArgsConstructor;
import org.example.commondto.ProductHistoryEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ProductHistoryEventProducer {
    private static final String PRODUCT_HISTORY_TOPIC = "product-history";

    private final KafkaTemplate<String, ProductHistoryEvent> productHistoryKafkaTemplate;

    public void sendProductHistoryEvent(String productId, String userId) {
        ProductHistoryEvent productHistoryEvent = new ProductHistoryEvent(productId, userId, Date.from(Instant.now()));
        productHistoryKafkaTemplate.send(PRODUCT_HISTORY_TOPIC, productHistoryEvent);
    }

}
