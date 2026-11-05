package com.peoplehub.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
@Entity
@Table(name="users")
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true) private String username;
 @Column(nullable=false, unique=true) private String email;
 @Column(nullable=false) private String password;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
 private boolean active=true;
 private LocalDateTime createdAt=LocalDateTime.now();
 protected User() {}
 public User(String username,String email,String password,Role role) { this.username=username; this.email=email; this.password=password; this.role=role; this.active=true; }
 public Long getId(){return id;} public String getUsername(){return username;} public String getEmail(){return email;}
 @JsonIgnore public String getPassword(){return password;} public Role getRole(){return role;} public boolean isActive(){return active;}
 public void setActive(boolean active){this.active=active;}
}
