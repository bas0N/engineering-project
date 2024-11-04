package org.example.product.repository;

import org.example.product.entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review, String> {

    @Query("{ 'parent_asin': ?0 }")
    List<Review> findAllByParent_asin(String parent_asin);
}
