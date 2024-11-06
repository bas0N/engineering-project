package org.example.history.listener;

import lombok.RequiredArgsConstructor;
import org.example.commondto.ProductHistoryEvent;
import org.example.history.entity.History;
import org.example.history.mapper.HistoryMapper;
import org.example.history.repository.HistoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductHistoryEventListener {
    private final HistoryRepository historyRepository;
    @KafkaListener(
            topics = "product-history-events",
            groupId = "history-service-group",
            containerFactory = "productHistoryKafkaListenerContainerFactory"
    )
    public void handleProductHistoryEvent(ProductHistoryEvent productHistoryEvent) {
        History history = HistoryMapper.INSTANCE.mapProductHistoryEventToHistory(productHistoryEvent);
        historyRepository.save(history);
    }
}
