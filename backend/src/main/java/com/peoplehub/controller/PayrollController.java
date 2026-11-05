package com.peoplehub.controller;

import com.peoplehub.dto.PayrollRequest;
import com.peoplehub.model.Payroll;
import com.peoplehub.repository.EmployeeRepository;
import com.peoplehub.repository.PayrollRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollRepository payroll;
    private final EmployeeRepository employees;

    public PayrollController(PayrollRepository p, EmployeeRepository e) {
        payroll = p;
        employees = e;
    }

    @GetMapping
    public List<Payroll> mineOrAll(Principal p) {
        boolean admin = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return admin
                ? payroll.findAll()
                : payroll.findByEmployeeUserUsernameOrderByPayMonthDesc(p.getName());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Payroll create(@Valid @RequestBody PayrollRequest r) {
        var employee = employees.findByIdAndUser_ActiveTrue(r.employeeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Active employee not found"));

        if (r.deductions().compareTo(r.basic().add(r.allowances())) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Deductions cannot exceed gross pay");
        }

        return payroll.save(new Payroll(
                employee,
                r.payMonth(),
                r.basic(),
                r.allowances(),
                r.deductions(),
                r.paidOn()));
    }
}
