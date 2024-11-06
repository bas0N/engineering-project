package org.example.basket.repository;

import org.example.basket.entity.Basket;
import org.example.basket.entity.BasketItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItems, Long> {
    @Query("SELECT SUM(quantity) from BasketItems where basket = ?1")
    Long sumBasketItems(long basket);

    @Query("SELECT bi FROM BasketItems bi WHERE bi.product = :product AND bi.basket = :basket")
    Optional<BasketItems> findBasketItemsByProductAndBasket(@Param("product") String product, @Param("basket") Basket basket);

    @Query("SELECT bi FROM BasketItems bi WHERE bi.basket = :basket AND bi.product = :product")
    Optional<BasketItems> findByBasketAndProduct(Basket basket,String product);

    @Query("SELECT bi FROM BasketItems bi WHERE bi.basket = :basket")
    List<BasketItems> findBasketItemsByBasket(Basket basket);
}
