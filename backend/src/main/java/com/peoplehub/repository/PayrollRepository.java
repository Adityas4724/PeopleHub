package com.peoplehub.repository;
import com.peoplehub.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PayrollRepository extends JpaRepository<Payroll,Long> { List<Payroll> findByEmployeeUserUsernameOrderByPayMonthDesc(String username);
}
