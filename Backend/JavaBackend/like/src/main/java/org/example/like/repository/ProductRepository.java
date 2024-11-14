package org.example.like.repository;

import org.example.like.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Product p WHERE p.uuid = :productId")
    boolean existsByProductId(String productId);

    @Query("SELECT l.product FROM Like l WHERE l.userId = :userId")
    List<Product> findLikedProductsByUserId(String userId);

    @Query("SELECT p FROM Product p WHERE p.uuid = :productId")
    Optional<Product> findByUuid(String productId);
}
