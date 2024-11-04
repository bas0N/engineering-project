package org.example.like.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.UnauthorizedException;
import org.example.like.response.LikeResponse;
import org.example.like.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/v1/like")
public class LikeController {
    private final LikeService likeService;

    @RequestMapping(path = "/{productId}", method = RequestMethod.POST)
    public ResponseEntity<?> addLike(@PathVariable String productId, HttpServletRequest request) {
        try {
            LikeResponse addLikeResponse = likeService.addLike(productId, request);
            return ResponseEntity.ok(addLikeResponse);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding a like: " + e.getMessage());
        }
    }

    @RequestMapping(path = "/my", method = RequestMethod.GET)
    public ResponseEntity<?> getMyLike(HttpServletRequest request) {
        return ResponseEntity.ok(likeService.getMyLikedProducts(request));
    }

    @RequestMapping(path = "/number/{productId}", method = RequestMethod.GET)
    public ResponseEntity<?> getNumberOfLikes(@PathVariable String productId) {
        return ResponseEntity.ok(likeService.getNumberOfLikes(productId));
    }

    @RequestMapping(path = "/remove/{likeId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeLike(@PathVariable String likeId, HttpServletRequest request) {
        try {
            likeService.removeLike(likeId, request);
            return ResponseEntity.ok("Like removed successfully.");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while removing a like: " + e.getMessage());
        }
    }

}
