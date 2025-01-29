package org.example.like.repository;

import org.example.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END FROM Like l WHERE l.userId = :userId AND l.product.uuid = :productId")
    boolean existsByUserIdAndProductId(String userId, String productId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.product.id = :productId")
    Long countByProductId(Long productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.uuid = :uuid")
    int deleteByUuid(String uuid);

    @Query("SELECT l FROM Like l WHERE l.uuid = :uuid")
    Optional<Like> findByUuid(String uuid);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END FROM Like l WHERE l.product.id = :id")
    boolean existsByProductId(long id);

    @Query("SELECT l FROM Like l WHERE l.userId = :userId AND l.product.uuid = :productId")
    Optional<Like> findByUserIdAndProductId(String userId, String productId);
}
