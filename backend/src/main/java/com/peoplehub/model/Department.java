package com.peoplehub.model;
import jakarta.persistence.*;
@Entity
public class Department {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String name;
 private String description;
 protected Department() {}
 public Department(String name,String description){this.name=name;this.description=description;}
 public Long getId(){return id;} public String getName(){return name;} public String getDescription(){return description;}
 public void setName(String name){this.name=name;} public void setDescription(String description){this.description=description;}
}
