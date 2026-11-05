
package com.peoplehub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.peoplehub.dto.CreateEmployeeRequest;
import com.peoplehub.dto.UpdateEmployeeRequest;
import com.peoplehub.model.Department;
import com.peoplehub.model.Employee;
import com.peoplehub.model.Role;
import com.peoplehub.model.User;
import com.peoplehub.repository.DepartmentRepository;
import com.peoplehub.repository.EmployeeRepository;
import com.peoplehub.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employees;
    private final UserRepository users;
    private final DepartmentRepository departments;
    private final PasswordEncoder encoder;

    public EmployeeController(
            EmployeeRepository e,
            UserRepository u,
            DepartmentRepository d,
            PasswordEncoder p) {

        employees = e;
        users = u;
        departments = d;
        encoder = p;
    }

    // Get all active employees
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Employee> all() {
        return employees.findAllByUser_ActiveTrue();
    }

    // Get logged-in employee profile
    @GetMapping("/me")
    public Employee me(java.security.Principal p) {
        return employees.findByUserUsername(p.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee profile not found"));
    }

    // Create employee
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Employee create(@Valid @RequestBody CreateEmployeeRequest r) {

        if (users.existsByUsername(r.username())
                || users.existsByEmail(r.email())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username or email already exists");
        }

        User u = users.save(new User(
                r.username(),
                r.email(),
                encoder.encode(r.temporaryPassword()),
                Role.EMPLOYEE));

        Department d = r.departmentId() == null
                ? null
                : departments.findById(r.departmentId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Department not found"));

        String code = "PH-" + String.format("%05d", u.getId());

        return employees.save(new Employee(
                code,
                r.fullName(),
                r.designation(),
                r.phone(),
                r.joiningDate(),
                u,
                d));
    }

    // Edit employee
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Employee update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest r) {

        Employee employee = employees.findByIdAndUser_ActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active employee not found"));

        Department department = r.departmentId() == null
                ? null
                : departments.findById(r.departmentId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Department not found"));

        employee.updateDetails(
                r.fullName(),
                r.designation(),
                r.phone(),
                r.joiningDate(),
                department);

        return employee;
    }

    // Delete employee safely (deactivate account)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String delete(@PathVariable Long id) {

        Employee employee = employees.findByIdAndUser_ActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active employee not found"));

        User user = employee.getUser();
        user.setActive(false);

        users.save(user);

        return "Employee deactivated successfully";
    }
}
