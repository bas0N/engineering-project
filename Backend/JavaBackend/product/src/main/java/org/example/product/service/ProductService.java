package org.example.product.service;

import lombok.RequiredArgsConstructor;
import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<Product> getProducts(int page, int limit, String sort, String mainCategory, String title, Double minPrice, Double maxPrice, Double minRating, Double maxRating, List<String> categories, String store) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit, Sort.by(sort).ascending());
        Page<Product> products;

        if (mainCategory != null || title != null || minPrice != null || maxPrice != null ||
                minRating != null || maxRating != null || categories != null || store != null) {

            products = productRepository.searchProducts(
                    mainCategory,
                    title != null ? ".*" + title + ".*" : null,
                    minPrice != null ? minPrice : Double.MIN_VALUE,
                    maxPrice != null ? maxPrice : Double.MAX_VALUE,
                    minRating != null ? minRating : 0.0,
                    maxRating != null ? maxRating : 5.0,
                    categories,
                    store,
                    pageRequest
            );
        } else {
            products = productRepository.findAll(pageRequest);
        }

        return products;
    }

}
