package org.example.order.repository;

import org.example.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    Optional<Order> findByUuid(String orderId);

    @Query("SELECT o FROM Order o WHERE o.client = :client")
    List<Order> findOrderByClient(String client);

    @Query("SELECT o FROM Order o WHERE o.uuid = :uuid")
    Optional<Order> findOrderByUuid(String uuid);

    @Query("SELECT o FROM Order o WHERE o.client = :userId")
    List<Order> findByClient(String userId);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.client = :userId")
    List<Order> findByClientWithItems(String userId);


    @Query("SELECT o FROM Order o WHERE o.uuid = :uuid")
    Optional<Order> getOrderByUuid(String uuid);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.uuid = :uuid")
    Optional<Order> findByUuidWithItems(String uuid);
}
