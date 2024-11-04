package org.example.product.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.entity.Product;
import org.example.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(required = false) String mainCategory,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String store
    ) {
        return productService.getProducts(page, limit, sort, mainCategory, title, minPrice, maxPrice, minRating, maxRating, categories, store);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getProduct(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @RequestMapping(path = "/visit/{id}", method = RequestMethod.GET)
    public void visitProduct(@PathVariable String id) {
        productService.visitProduct(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Product createProduct(@Valid @RequestBody AddProductRequest addProductRequest, HttpServletRequest request) {
        return productService.createProduct(addProductRequest, request);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public Product updateProduct(@PathVariable String id, @Valid @RequestBody AddProductRequest addProductRequest, HttpServletRequest request) {
        return productService.updateProduct(id, addProductRequest, request);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }
}
