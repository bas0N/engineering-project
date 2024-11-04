package org.example.product.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.AbstractDocument;
import java.util.List;


public interface ProductService {
    ResponseEntity<?> getProducts(int page, int limit, String sort, String mainCategory, String title, Double minPrice, Double maxPrice, Double minRating, Double maxRating, List<String> categories, String store);
    ResponseEntity<?> getProductById(String id);

    void visitProduct(String id);

    Product createProduct(AddProductRequest addProductRequest, HttpServletRequest request);

    Product updateProduct(String id, AddProductRequest addProductRequest, HttpServletRequest request);

    void deleteProduct(String id);
}
