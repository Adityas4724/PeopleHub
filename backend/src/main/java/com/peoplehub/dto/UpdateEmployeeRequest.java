
package com.peoplehub.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record UpdateEmployeeRequest(
    @NotBlank String fullName,
    String designation,
    String phone,
    LocalDate joiningDate,
    Long departmentId
) {}
