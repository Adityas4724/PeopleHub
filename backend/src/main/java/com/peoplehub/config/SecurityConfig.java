package com.peoplehub.config;

import com.peoplehub.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
 private final JwtAuthFilter jwtAuthFilter;
 @Value("${app.cors.allowed-origin}") private String allowedOrigin;
 public SecurityConfig(JwtAuthFilter jwtAuthFilter) { this.jwtAuthFilter = jwtAuthFilter; }

 @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
 @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception { return c.getAuthenticationManager(); }

 @Bean CorsConfigurationSource corsConfigurationSource() {
  CorsConfiguration c = new CorsConfiguration();
  c.setAllowedOrigins(List.of(allowedOrigin));
  c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
  c.setAllowedHeaders(List.of("Authorization","Content-Type"));
  c.setExposedHeaders(List.of("Authorization"));
  c.setAllowCredentials(true);
  UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
  s.registerCorsConfiguration("/**", c);
  return s;
 }

 @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  return http.csrf(csrf -> csrf.disable())
   .cors(cors -> {})
   .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a -> a
    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    .requestMatchers("/api/auth/**", "/api/health").permitAll()
    .anyRequest().authenticated())
   .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
   .build();
 }
}
