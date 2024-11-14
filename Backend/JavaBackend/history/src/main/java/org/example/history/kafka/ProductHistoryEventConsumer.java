package org.example.history.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.commondto.ProductHistoryEvent;
import org.example.history.entity.History;
import org.example.history.mapper.HistoryMapper;
import org.example.history.repository.HistoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductHistoryEventConsumer {
    private final HistoryRepository historyRepository;
    @KafkaListener(
            topics = "product-history",
            groupId = "history-service-group",
            containerFactory = "productHistoryKafkaListenerContainerFactory"
    )
    public void handleProductHistoryEvent(ProductHistoryEvent productHistoryEvent, Acknowledgment ack) throws Exception {
        try{
            History history = HistoryMapper.INSTANCE.toHistory(productHistoryEvent);
            historyRepository.save(history);
            ack.acknowledge();
        } catch (ResourceNotFoundException e) {
            ack.acknowledge();
        } catch (Exception e) {
            throw new Exception("Error while handling product history event", e);
        }
    }
}
