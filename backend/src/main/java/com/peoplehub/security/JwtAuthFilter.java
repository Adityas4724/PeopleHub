package com.peoplehub.security;

import com.peoplehub.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
 private final JwtService jwtService;
 private final UserRepository users;
 public JwtAuthFilter(JwtService jwtService, UserRepository users) { this.jwtService = jwtService; this.users = users; }

 @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
   throws ServletException, IOException {
  String header = request.getHeader("Authorization");
  if (header != null && header.startsWith("Bearer ")) {
   String token = header.substring(7);
   try {
    if (jwtService.isValid(token)) {
     String username = jwtService.extractUsername(token);
     users.findByUsername(username).filter(u -> u.isActive()).ifPresent(u -> {
      var auth = new UsernamePasswordAuthenticationToken(u.getUsername(), null,
       List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name())));
      SecurityContextHolder.getContext().setAuthentication(auth);
     });
    }
   } catch (RuntimeException ignored) { SecurityContextHolder.clearContext(); }
  }
  chain.doFilter(request, response);
 }
}
