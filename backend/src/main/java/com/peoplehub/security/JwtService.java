package com.peoplehub.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
 private final SecretKey key;
 private final long expirationMs;
 public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long expirationMs) {
  byte[] decoded = Base64.getDecoder().decode(secret);
  if (decoded.length < 32) throw new IllegalArgumentException("JWT_SECRET must decode to at least 32 bytes");
  this.key = Keys.hmacShaKeyFor(decoded);
  this.expirationMs = expirationMs;
 }
 public String generate(String username, String role) {
  Date now = new Date();
  return Jwts.builder().subject(username).claim("role", role).issuedAt(now)
   .expiration(new Date(now.getTime() + expirationMs)).signWith(key).compact();
 }
 public String extractUsername(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject(); }
 public boolean isValid(String token) {
  try { Jwts.parser().verifyWith(key).build().parseSignedClaims(token); return true; }
  catch (RuntimeException ex) { return false; }
 }
}
