package org.example.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.ChangePasswordData;
import org.example.auth.dto.LoginResponse;
import org.example.auth.dto.UserRegisterDto;
import org.example.auth.entity.*;
import org.example.auth.exceptions.UserDontExistException;
import org.example.auth.exceptions.UserExistingWithMail;
import org.example.auth.exceptions.UserExistingWithName;
import org.example.auth.repository.ResetOperationsRepository;
import org.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResetOperationService resetOperationService;
    private final ResetOperationsRepository resetOperationsRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final CookieService cookieService;
    @Value("${jwt.exp}")
    private int exp;
    @Value("${jwt.refresh.exp}")
    private int refreshExp;


    private User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    private String generateToken(String username,int exp) {
        return jwtService.generateToken(username,exp);
    }


    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        log.info("Delete all cookies");
        Cookie cookie = cookieService.removeCookie(request.getCookies(),"Authorization");
        if (cookie != null){
            response.addCookie(cookie);
        }
        cookie = cookieService.removeCookie(request.getCookies(),"refresh");
        if (cookie != null){
            response.addCookie(cookie);
        }
        return  ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
    }

    public void validateToken(HttpServletRequest request,HttpServletResponse response) throws ExpiredJwtException, IllegalArgumentException{
        String token = null;
        String refresh = null;
        if (request.getCookies() != null){
            for (Cookie value : Arrays.stream(request.getCookies()).toList()) {
                if (value.getName().equals("Authorization")) {
                    token = value.getValue();
                } else if (value.getName().equals("refresh")) {
                    refresh = value.getValue();
                }
            }
        }else {
            log.info("Can't login because in token is empty");
            throw new IllegalArgumentException("Token can't be null");
        }
        try {
            jwtService.validateToken(token);
        }catch (IllegalArgumentException | ExpiredJwtException e){
            jwtService.validateToken(refresh);
            Cookie refreshCokkie = cookieService.generateCookie("refresh", jwtService.refreshToken(refresh,refreshExp), refreshExp);
            Cookie cookie = cookieService.generateCookie("Authorization", jwtService.refreshToken(refresh,exp), exp);
            response.addCookie(cookie);
            response.addCookie(refreshCokkie);
        }

    }

    public ResponseEntity<LoginResponse> loggedIn(HttpServletRequest request, HttpServletResponse response){
        try{
            validateToken(request, response);
            return ResponseEntity.ok(new LoginResponse(true));
        }catch (ExpiredJwtException|IllegalArgumentException e){
            return ResponseEntity.ok(new LoginResponse(false));
        }
    }
    public ResponseEntity<?> loginByToken(HttpServletRequest request, HttpServletResponse response){
        try {
            validateToken(request, response);
            String refresh = null;
            for (Cookie value : Arrays.stream(request.getCookies()).toList()) {
                if (value.getName().equals("refresh")) {
                    refresh = value.getValue();
                }
            }
            String login = jwtService.getSubject(refresh);
            User user = userRepository.findUserByLoginAndLockAndEnabled(login).orElse(null);
            if (user != null){
                return ResponseEntity.ok(
                        UserRegisterDto
                                .builder()
                                .login(user.getUsername())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build());
            }
            log.info("Can't login user don't exist");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(Code.A1));
        }catch (ExpiredJwtException|IllegalArgumentException e){
            log.info("Can't login token is expired or null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(Code.A3));
        }
    }


    public void register(UserRegisterDto userRegisterDTO) throws UserExistingWithName, UserExistingWithMail {
        userRepository.findUserByLogin(userRegisterDTO.getLogin()).ifPresent(value->{
            log.info("Users alredy exist with this name");
            throw new UserExistingWithName("Users alredy exist with this name");
        });
        userRepository.findUserByEmail(userRegisterDTO.getEmail()).ifPresent(value->{
            log.info("Users alredy exist with this mail");
            throw new UserExistingWithMail("Users alredy exist with this mail");
        });
        User user = new User();
        user.setLock(true);
        user.setEnabled(false);
        user.setLogin(userRegisterDTO.getLogin());
        user.setPassword(userRegisterDTO.getPassword());
        user.setEmail(userRegisterDTO.getEmail());
        user.setRole(Role.USER);

        saveUser(user);
        //emailService.sendActivation(user);
    }

    public ResponseEntity<?> login(HttpServletResponse response, User authRequest) {
        log.info("--START LoginService");
        User user = userRepository.findUserByLoginAndLockAndEnabled(authRequest.getUsername()).orElse(null);
        if (user != null) {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authenticate.isAuthenticated()) {
                Cookie refresh = cookieService.generateCookie("refresh", generateToken(authRequest.getUsername(),refreshExp), refreshExp);
                Cookie cookie = cookieService.generateCookie("Authorization", generateToken(authRequest.getUsername(),exp), exp);
                response.addCookie(cookie);
                response.addCookie(refresh);
                return ResponseEntity.ok(
                        UserRegisterDto
                                .builder()
                                .login(user.getUsername())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build());
            } else {
                log.info("--STOP LoginService");
                return ResponseEntity.ok(new AuthResponse(Code.A1));
            }
        }
        log.info("User dont exist");
        log.info("--STOP LoginService");
        return ResponseEntity.ok(new AuthResponse(Code.A2));
    }



    public void setAsAdmin(UserRegisterDto user) {
        userRepository.findUserByLogin(user.getLogin()).ifPresent(value->{
            value.setRole(Role.ADMIN);
            userRepository.save(value);
        });
    }

    public void activateUser(String uid) throws UserDontExistException {
        User user = userRepository.findUserByUuid(uid).orElse(null);
        if (user != null){
            user.setLock(false);
            user.setEnabled(true);
            userRepository.save(user);
            return;
        }
        throw new UserDontExistException("User dont exist");
    }

    public void recoveryPassword(String email) throws UserDontExistException{
        User user = userRepository.findUserByEmail(email).orElse(null);
        if (user != null){
            ResetOperations resetOperations = resetOperationService.initResetOperation(user);
            emailService.sendPasswordRecovery(user,resetOperations.getUid());
            return;
        }
        throw new UserDontExistException("User dont exist");
    }

    public void restPassword(ChangePasswordData changePasswordData) throws UserDontExistException{
        ResetOperations resetOperations = resetOperationsRepository.findByUid(changePasswordData.getUid()).orElse(null);
        if (resetOperations != null){
            User user = userRepository.findUserByUuid(resetOperations.getUser().getUuid()).orElse(null);

            if (user != null){
                user.setPassword(changePasswordData.getPassword());
                saveUser(user);
                resetOperationService.endOperation(resetOperations.getUid());
                return;
            }
        }
        throw new UserDontExistException("User dont exist");
    }
    public void authorize(HttpServletRequest request) throws UserDontExistException{
        String token = null;
        String refresh = null;
        if (request.getCookies() != null){
            for (Cookie value : Arrays.stream(request.getCookies()).toList()) {
                if (value.getName().equals("Authorization")) {
                    token = value.getValue();
                } else if (value.getName().equals("refresh")) {
                    refresh = value.getValue();
                }
            }
        }else {
            log.info("Can't login because in token is empty");
            throw new IllegalArgumentException("Token can't be null");
        }
        if (token != null && !token.isEmpty()){
            String subject = jwtService.getSubject(token);
            userRepository.findUserByLoginAndLockAndEnabledAndIsAdmin(subject).orElseThrow(()->new UserDontExistException("User not found"));
        } else if (refresh != null && !refresh.isEmpty()) {
            String subject = jwtService.getSubject(refresh);
            userRepository.findUserByLoginAndLockAndEnabledAndIsAdmin(subject).orElseThrow(()->new UserDontExistException("User not found"));
        }
    }
}
