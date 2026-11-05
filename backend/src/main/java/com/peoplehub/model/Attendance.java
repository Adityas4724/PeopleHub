package com.peoplehub.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"employee_id","work_date"}))
public class Attendance {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private Employee employee;
 @Column(name="work_date",nullable=false) private LocalDate workDate;
 private LocalDateTime checkIn;
 private LocalDateTime checkOut;
 protected Attendance() {}
 public Attendance(Employee employee,LocalDate date){this.employee=employee;this.workDate=date;}
 public Long getId(){return id;} public Employee getEmployee(){return employee;} public LocalDate getWorkDate(){return workDate;}
 public LocalDateTime getCheckIn(){return checkIn;} public LocalDateTime getCheckOut(){return checkOut;}
 public void setCheckIn(LocalDateTime v){checkIn=v;} public void setCheckOut(LocalDateTime v){checkOut=v;}
}
