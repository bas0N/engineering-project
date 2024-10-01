package org.example.like.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.like.dto.LikeDto;
import org.example.like.response.LikeResponse;
import org.example.like.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/like")
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final LikeService likeService;

    @RequestMapping(path = "/{productId}", method = RequestMethod.POST)
    public ResponseEntity<LikeResponse> addLike(@PathVariable String productId) {
        LikeResponse addLikeResponse = likeService.addLike(productId);
        return ResponseEntity.ok(addLikeResponse);
    }

    @RequestMapping(path = "/my", method = RequestMethod.GET)
    public ResponseEntity<LikeDto> getMyLike() {
        LikeDto like = likeService.getMyLike();
        return ResponseEntity.ok(like);
    }
}
