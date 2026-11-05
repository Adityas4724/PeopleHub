package com.peoplehub.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record PayrollRequest(@NotNull Long employeeId,@NotBlank String payMonth,@NotNull @DecimalMin("0.0") BigDecimal basic,
 @NotNull @DecimalMin("0.0") BigDecimal allowances,@NotNull @DecimalMin("0.0") BigDecimal deductions,LocalDate paidOn) {}
