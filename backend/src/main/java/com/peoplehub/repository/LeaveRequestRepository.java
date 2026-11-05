package com.peoplehub.repository;
import com.peoplehub.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,Long> { List<LeaveRequest> findByEmployeeUserUsernameOrderByCreatedAtDesc(String username);
}
