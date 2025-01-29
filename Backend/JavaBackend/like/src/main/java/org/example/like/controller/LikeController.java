package org.example.like.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.like.dto.IsLikeResponse;
import org.example.like.dto.ProductResponse;
import org.example.like.response.LikeResponse;
import org.example.like.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {
    private final LikeService likeService;

    @RequestMapping(path = "/{productId}", method = RequestMethod.POST)
    public ResponseEntity<LikeResponse> addLike(@PathVariable String productId, HttpServletRequest request) {
        return likeService.addLike(productId, request);
    }

    @RequestMapping(path = "/my", method = RequestMethod.GET)
    public ResponseEntity<List<ProductResponse>> getMyLike(HttpServletRequest request) {
        return likeService.getMyLikedProducts(request);
    }

    @RequestMapping(path = "/number/{productId}", method = RequestMethod.GET)
    public ResponseEntity<Long> getNumberOfLikes(@PathVariable String productId) {
        return likeService.getNumberOfLikes(productId);
    }

    @RequestMapping(path = "/remove/{likeId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeLike(@PathVariable String likeId, HttpServletRequest request) {
        return likeService.removeLike(likeId, request);
    }

    @RequestMapping(path = "/isLiked/{productId}", method = RequestMethod.GET)
    public ResponseEntity<IsLikeResponse> isLiked(@PathVariable String productId, HttpServletRequest request) {
        return likeService.isLiked(productId, request);
    }

}
