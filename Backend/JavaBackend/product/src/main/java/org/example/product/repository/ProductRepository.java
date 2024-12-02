package org.example.product.repository;

import org.example.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{ 'parent_asin': ?0 }")
    Optional<Product> findByParentAsin(String parentAsin);

    @Query(value = "{ 'parent_asin': ?0 }", delete = true)
    void deleteByParentAsin(String parentAsin);

    @Query("{ 'userId': ?0 }")
    Page<Product> findByUserId(String userId, Pageable pageable);
}
