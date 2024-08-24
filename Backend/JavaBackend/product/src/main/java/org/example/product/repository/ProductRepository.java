package org.example.product.repository;

import org.example.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{ $and: [ " +
            "{ 'mainCategory': ?0 }, " +
            "{ 'title': { $regex: ?1, $options: 'i' } }, " +
            "{ 'price': { $gte: ?2, $lte: ?3 } }, " +
            "{ 'averageRating': { $gte: ?4, $lte: ?5 } }, " +
            "{ $or: [ { 'categories': { $in: ?6 } }, { ?6: null } ] }, " +
            "{ $or: [ { 'store': ?7 }, { ?7: null } ] } " +
            "]}")
    Page<Product> searchProducts(
            String mainCategory,
            String title,
            Double minPrice,
            Double maxPrice,
            Double minRating,
            Double maxRating,
            List<String> categories,
            String store,
            Pageable pageable
    );
}
