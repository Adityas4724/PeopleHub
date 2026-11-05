package com.peoplehub.service;
import com.peoplehub.dto.*;
import com.peoplehub.repository.UserRepository;
import com.peoplehub.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;
@Service
public class AuthService {
 private final AuthenticationManager manager; private final UserRepository users; private final JwtService jwt;
 public AuthService(AuthenticationManager manager,UserRepository users,JwtService jwt){this.manager=manager;this.users=users;this.jwt=jwt;}
 public LoginResponse login(LoginRequest request){
  manager.authenticate(new UsernamePasswordAuthenticationToken(request.username(),request.password()));
  var user=users.findByUsername(request.username()).orElseThrow();
  if(!user.isActive()) throw new BadCredentialsException("Account is inactive");
  return new LoginResponse(jwt.generate(user.getUsername(),user.getRole().name()),user.getUsername(),user.getRole().name());
 }
}
