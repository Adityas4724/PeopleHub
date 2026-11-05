package com.peoplehub.dto;
import jakarta.validation.constraints.NotBlank;
public record DepartmentRequest(@NotBlank String name,String description) {}
