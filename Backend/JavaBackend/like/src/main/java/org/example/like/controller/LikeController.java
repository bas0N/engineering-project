package org.example.like.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.like.dto.ProductResponse;
import org.example.like.response.LikeResponse;
import org.example.like.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/v1/like")
public class LikeController {
    private final LikeService likeService;

    @RequestMapping(path = "/{productId}", method = RequestMethod.POST)
    public ResponseEntity<LikeResponse> addLike(@PathVariable String productId, HttpServletRequest request) {
        LikeResponse addLikeResponse = likeService.addLike(productId, request);
        return ResponseEntity.ok(addLikeResponse);
    }

    @RequestMapping(path = "/my", method = RequestMethod.GET)
    public ResponseEntity<List<ProductResponse>> getMyLike(HttpServletRequest request) {
        return ResponseEntity.ok(likeService.getMyLikedProducts(request));
    }

    @RequestMapping(path = "/number/{productId}", method = RequestMethod.GET)
    public ResponseEntity<Long> getNumberOfLikes(@PathVariable String productId) {
        return ResponseEntity.ok(likeService.getNumberOfLikes(productId));
    }

    @RequestMapping(path = "/remove/{likeId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeLike(@PathVariable String likeId, HttpServletRequest request) {
        likeService.removeLike(likeId, request);
        return ResponseEntity.ok("Like removed successfully.");
    }

    @RequestMapping(path = "/isLiked/{productId}", method = RequestMethod.GET)
    public ResponseEntity<Boolean> isLiked(@PathVariable String productId, HttpServletRequest request) {
        return ResponseEntity.ok(likeService.isLiked(productId, request));
    }

}
