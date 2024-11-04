package org.example.history.listener;

import org.example.commondto.ProductHistoryEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductHistoryEventListener {
    @KafkaListener(
            topics = "product-history-events",
            groupId = "history-service-group",
            containerFactory = "productHistoryKafkaListenerContainerFactory"
    )
    public void handleProductHistoryEvent(ProductHistoryEvent productHistoryEvent) {

    }
}
