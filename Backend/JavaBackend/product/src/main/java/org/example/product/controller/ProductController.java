package org.example.product.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.dto.Request.UpdateProductRequest;
import org.example.product.dto.Response.ImageUploadResponse;
import org.example.product.dto.Response.ProductDetailResponse;
import org.example.product.dto.Response.ProductResponse;
import org.example.product.service.ProductService;
import org.springframework.data.domain.Page;
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

    @GetMapping(path = "/search")
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

    @GetMapping(path = "/{productId}")
    public ResponseEntity<ProductDetailResponse> getProduct(@PathVariable String productId, HttpServletRequest request) {
        return productService.getProductById(productId, request);
    }

    @GetMapping(path = "/my-products")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(HttpServletRequest request, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit) {
        return productService.getMyProducts(request, page, limit);
    }

    @PostMapping
    public ResponseEntity<ProductDetailResponse> createProduct(@RequestBody @Valid AddProductRequest addProductRequest, HttpServletRequest request) {
        return productService.createProduct(addProductRequest, request);
    }

    @PatchMapping(path = "/{productId}")
    public ResponseEntity<ProductDetailResponse> updateProduct(@PathVariable String productId,
                                                               @Valid @RequestBody UpdateProductRequest updateProductRequest,
                                                               HttpServletRequest request) {
        return productService.updateProduct(productId, updateProductRequest, request);
    }

    @PostMapping(path = "/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImage(@PathVariable String productId,
                                                           @RequestParam("hi_res") MultipartFile hi_res,
                                                           @RequestParam("large") MultipartFile large,
                                                           @RequestParam("thumb") MultipartFile thumb,
                                                           @RequestParam("variant") String variant,
                                                           HttpServletRequest request) {
        return productService.addImageToProduct(productId, hi_res, large, thumb, variant, request);
    }

    @PatchMapping(path = "/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> updateImage(@PathVariable String productId,
                                                           @RequestParam("hi_res") MultipartFile hi_res,
                                                           @RequestParam("large") MultipartFile large,
                                                           @RequestParam("thumb") MultipartFile thumb,
                                                           @RequestParam("variant") String variant,
                                                           @RequestParam("order") int order,
                                                           HttpServletRequest request) {
        return productService.updateImage(productId, hi_res, large, thumb, variant, order, request);
    }

    @DeleteMapping(path = "/{productId}/image/{order}")
    public ResponseEntity<String> deleteImage(@PathVariable String productId, @PathVariable("order") int order, HttpServletRequest request) {
        return productService.deleteImage(productId, order, request);
    }

    @DeleteMapping(path = "/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId, HttpServletRequest request) {
        return productService.deleteProduct(productId, request);
    }
}
