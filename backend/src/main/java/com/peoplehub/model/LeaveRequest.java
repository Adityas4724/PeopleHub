package com.peoplehub.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
public class LeaveRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private Employee employee;
 @Column(nullable=false) private LocalDate startDate;
 @Column(nullable=false) private LocalDate endDate;
 @Column(nullable=false,length=1000) private String reason;
 @Enumerated(EnumType.STRING) private LeaveStatus status=LeaveStatus.PENDING;
 private LocalDateTime createdAt=LocalDateTime.now();
 protected LeaveRequest() {}
 public LeaveRequest(Employee e,LocalDate s,LocalDate end,String reason){this.employee=e;this.startDate=s;this.endDate=end;this.reason=reason;}
 public Long getId(){return id;} public Employee getEmployee(){return employee;} public LocalDate getStartDate(){return startDate;}
 public LocalDate getEndDate(){return endDate;} public String getReason(){return reason;} public LeaveStatus getStatus(){return status;}
 public void setStatus(LeaveStatus status){this.status=status;}
}
