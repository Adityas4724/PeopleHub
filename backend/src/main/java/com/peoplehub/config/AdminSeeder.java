package com.peoplehub.config;
import com.peoplehub.model.Role;
import com.peoplehub.model.User;
import com.peoplehub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class AdminSeeder {
 @Bean CommandLineRunner seedAdmin(UserRepository users,PasswordEncoder encoder,
  @Value("${ADMIN_USERNAME:}") String username,@Value("${ADMIN_EMAIL:}") String email,@Value("${ADMIN_PASSWORD:}") String password) {
  return args -> {
   if(username.isBlank() || email.isBlank() || password.isBlank()) {
    System.out.println("PeopleHub: admin bootstrap skipped; set ADMIN_USERNAME, ADMIN_EMAIL and ADMIN_PASSWORD.");
    return;
   }
   if(!users.existsByUsername(username) && !users.existsByEmail(email))
    users.save(new User(username,email,encoder.encode(password),Role.ADMIN));
  };
 }
}
