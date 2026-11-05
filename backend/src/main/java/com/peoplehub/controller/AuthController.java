package com.peoplehub.controller;
import com.peoplehub.dto.*;
import com.peoplehub.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final AuthService auth;
 public AuthController(AuthService auth){this.auth=auth;}
 @PostMapping("/login") public LoginResponse login(@Valid @RequestBody LoginRequest r){return auth.login(r);}
}
