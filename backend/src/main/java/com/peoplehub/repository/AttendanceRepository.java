package com.peoplehub.repository;
import com.peoplehub.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface AttendanceRepository extends JpaRepository<Attendance,Long> { Optional<Attendance> findByEmployeeIdAndWorkDate(Long employeeId, java.time.LocalDate date); List<Attendance> findByEmployeeUserUsernameOrderByWorkDateDesc(String username);
}
