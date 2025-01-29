package org.example.product.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.product.dto.Request.AddProductRequest;
import org.example.product.dto.Request.UpdateProductRequest;
import org.example.product.dto.Response.ImageUploadResponse;
import org.example.product.dto.Response.ProductDetailResponse;
import org.example.product.dto.Response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ProductService {
    ResponseEntity<Page<ProductResponse>> getProducts(int page, int limit, String sort, String mainCategory, String title, Double minPrice, Double maxPrice, Double minRating, Double maxRating, List<String> categories, String store);

    ResponseEntity<ProductDetailResponse> getProductById(String id, HttpServletRequest request);

    ResponseEntity<ProductDetailResponse> createProduct(AddProductRequest addProductRequest, HttpServletRequest request);

    ResponseEntity<ProductDetailResponse> updateProduct(String id, UpdateProductRequest updateProductRequest, HttpServletRequest request);

    ResponseEntity<String> deleteProduct(String id, HttpServletRequest request);

    ResponseEntity<ImageUploadResponse> addImageToProduct(String productId, MultipartFile hi_Res, MultipartFile large, MultipartFile thumb, String variant, HttpServletRequest request);

    ResponseEntity<ImageUploadResponse> updateImage(String productId, MultipartFile hiRes, MultipartFile large, MultipartFile thumb, String variant, int order, HttpServletRequest request);

    ResponseEntity<String> deleteImage(String productId, int order, HttpServletRequest request);

    ResponseEntity<Page<ProductResponse>> getMyProducts(HttpServletRequest request, int page, int limit);
}
