
package com.peoplehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peoplehub.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUserUsername(String username);

    List<Employee> findAllByUser_ActiveTrue();

    Optional<Employee> findByIdAndUser_ActiveTrue(Long id);
}
