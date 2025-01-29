package org.example.order.repository;

import org.example.order.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<OrderItems, Long> {
    @Query("SELECT i FROM OrderItems i WHERE i.order.id = :orderId")
    List<OrderItems> findByOrder(Long orderId);

}
