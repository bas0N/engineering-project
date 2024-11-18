package org.example.product.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.dto.Request.UpdateProductRequest;
import org.example.product.dto.Response.ImageUploadResponse;
import org.example.product.dto.Response.ProductResponse;
import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
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

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid AddProductRequest addProductRequest, HttpServletRequest request) {
        ProductResponse product = productService.createProduct(addProductRequest, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @RequestMapping(path = "/{productId}", method = RequestMethod.PATCH)
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String productId,
                                                           @Valid @RequestBody UpdateProductRequest updateProductRequest,
                                                           HttpServletRequest request) {
       return ResponseEntity.ok(productService.updateProduct(productId, updateProductRequest, request));
    }

    @RequestMapping(path = "/{productId}/image", method = RequestMethod.POST)
    public ResponseEntity<?> uploadImage(@PathVariable String productId,
                                                           @RequestParam("hi_res") MultipartFile hi_res,
                                                           @RequestParam("large") MultipartFile large,
                                                           @RequestParam("thumb") MultipartFile thumb,
                                                           @RequestParam("variant") String variant,
                                                           HttpServletRequest request) {
        return productService.addImageToProduct(productId, hi_res, large, thumb, variant, request);
    }

    @RequestMapping(path = "/{productId}/image", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateImage(@PathVariable String productId,
                                                           @RequestParam("hi_res") MultipartFile hi_res,
                                                           @RequestParam("large") MultipartFile large,
                                                           @RequestParam("thumb") MultipartFile thumb,
                                                           @RequestParam("variant") String variant,
                                                           @RequestParam("order") int order,
                                                           HttpServletRequest request) {
        return productService.updateImage(productId, hi_res, large, thumb, variant, order, request);
    }

    @RequestMapping(path = "/{productId}/image", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteImage(@PathVariable String productId, @RequestParam("order") int order, HttpServletRequest request) {
        productService.deleteImage(productId, order, request);
        return ResponseEntity.ok("Image deleted successfully");
    }


    @RequestMapping(path = "/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteProduct(@PathVariable String productId, HttpServletRequest request) {
        productService.deleteProduct(productId, request);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
