package org.example.product.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.dto.Response.ProductResponse;
import org.example.product.entity.Product;
import org.example.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public ResponseEntity<Page<ProductResponse>> getProducts(
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

    @RequestMapping(path = "/{productId}", method = RequestMethod.GET)
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId, HttpServletRequest request) {
        return productService.getProductById(productId, request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(@RequestPart("product") @Valid AddProductRequest addProductRequest, HttpServletRequest request) {
        Product product = productService.createProduct(addProductRequest, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Product> updateProduct(@PathVariable String id,
                                                 @Valid @RequestBody AddProductRequest addProductRequest,
                                                 HttpServletRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, addProductRequest, request));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteProduct(@PathVariable String id, HttpServletRequest request) {
        productService.deleteProduct(id, request);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
