package com.peoplehub.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record CreateEmployeeRequest(@NotBlank String username,@Email @NotBlank String email,@NotBlank String temporaryPassword,
 @NotBlank String fullName,String designation,String phone,LocalDate joiningDate,Long departmentId) {}
