package org.example.auth.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.*;
import org.example.auth.dto.request.ChangePasswordData;
import org.example.auth.dto.request.LoginRequest;
import org.example.auth.dto.request.ResetPasswordData;
import org.example.auth.dto.response.AuthResponse;
import org.example.auth.entity.Code;
import org.example.auth.message.ValidationMessage;
import org.example.auth.service.UserService;
import org.example.exception.exceptions.ApiRequestException;
import org.example.exception.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody UserRegisterRequest user) {
        userService.register(user);
        return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        userService.login(response, loginRequest);
        return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
    }

    @RequestMapping(path = "/auto-login", method = RequestMethod.GET)
    public ResponseEntity<?> autoLogin(HttpServletResponse response, HttpServletRequest request) {
        userService.loginByToken(request, response);
        return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
    }

    @RequestMapping(path = "/logged-in", method = RequestMethod.GET)
    public ResponseEntity<?> loggedIn(HttpServletResponse response, HttpServletRequest request) {
        userService.loggedIn(request, response);
        return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout(HttpServletResponse response, HttpServletRequest request) {
        return userService.logout(request, response);
    }

    @RequestMapping(path = "/validate", method = RequestMethod.GET)
    public ResponseEntity<AuthResponse> validateToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("--START validateToken");
            userService.validateToken(request, response);  // Walidacja tokena w serwisie
            log.info("--STOP validateToken");
            return ResponseEntity.ok(new AuthResponse(Code.PERMIT));  // Token poprawny
        } catch (ExpiredJwtException e) {
            log.info("Token has expired");
            throw new UnauthorizedException("Token has expired", "TOKEN_EXPIRED");
        } catch (IllegalArgumentException e) {
            log.info("Token is invalid");
            throw new ApiRequestException("Token is invalid", "INVALID_TOKEN");
        }
    }

    @RequestMapping(path = "/authorize", method = RequestMethod.GET)
    public ResponseEntity<AuthResponse> authorize(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("--START authorize");
            userService.validateToken(request, response);  // Walidacja tokena
            userService.authorize(request);  // Autoryzacja użytkownika
            log.info("--STOP authorize");
            return ResponseEntity.ok(new AuthResponse(Code.PERMIT));  // Użytkownik autoryzowany
        } catch (ExpiredJwtException e) {
            log.info("Token is expired.");
            throw new UnauthorizedException("Token is expired.", "TOKEN_EXPIRED");
        } catch (IllegalArgumentException e) {
            log.info("Token is invalid.");
            throw new ApiRequestException("Invalid token.", "INVALID_TOKEN");
        }
    }

    @RequestMapping(path = "/activate", method = RequestMethod.GET)
    public ResponseEntity<AuthResponse> activateUser(@RequestParam String uid) {
//        try{
//            log.info("--START activateUser");
//            userService.activateUser(uid);
//            log.info("--STOP activateUser");
//            return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
//        }catch (UserDontExistException e){
//            log.info("User dont exist in database");
//            return ResponseEntity.status(400).body(new AuthResponse(Code.A6));
//        }
        return null;
    }

    @RequestMapping(path = "/reset-password", method = RequestMethod.POST)
    public ResponseEntity<AuthResponse> sendMailRecovery(@RequestBody ResetPasswordData resetPasswordData) {
//        try{
//            log.info("--START sendMailRecovery");
//            userService.recoveryPassword(resetPasswordData.getEmail());
//            log.info("--STOP sendMailRecovery");
//            return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
//        }catch (UserDontExistException e){
//            log.info("User dont exist in database");
//            return ResponseEntity.status(400).body(new AuthResponse(Code.A6));
//        }
        return null;
    }

    @RequestMapping(path = "/reset-password", method = RequestMethod.PATCH)
    public ResponseEntity<AuthResponse> recoveryMail(@RequestBody ChangePasswordData changePasswordData) {
//        try{
//            log.info("--START recoveryMail");
//            userService.restPassword(changePasswordData);
//            log.info("--STOP recoveryMail");
//            return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
//        }catch (UserDontExistException e){
//            log.info("User dont exist in database");
//            return ResponseEntity.status(400).body(new AuthResponse(Code.A6));
//        }
        return null;
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationMessage handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        return new ValidationMessage(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }
}
