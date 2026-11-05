package com.peoplehub.security;
import com.peoplehub.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
 private final UserRepository users;
 public UserDetailsServiceImpl(UserRepository users){this.users=users;}
 @Override public UserDetails loadUserByUsername(String username){
  var u=users.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
  return User.withUsername(u.getUsername()).password(u.getPassword()).roles(u.getRole().name()).disabled(!u.isActive()).build();
 }
}
