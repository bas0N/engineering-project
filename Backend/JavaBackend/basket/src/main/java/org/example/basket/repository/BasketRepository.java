package org.example.basket.repository;

import jakarta.transaction.Transactional;
import org.example.basket.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
    @Query("SELECT b FROM Basket b WHERE b.uuid = :uuid")
    Optional<Basket> findByUuid(String uuid);

    @Query("SELECT b FROM Basket b WHERE b.ownerId = :userId")
    Optional<Basket> findByOwnerId(String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Basket b WHERE b.uuid = :uuid")
    int deleteByUuid(String uuid);
}
