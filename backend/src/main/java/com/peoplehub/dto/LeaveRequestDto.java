package com.peoplehub.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public record LeaveRequestDto(@NotNull LocalDate startDate,@NotNull LocalDate endDate,@NotBlank String reason) {}
