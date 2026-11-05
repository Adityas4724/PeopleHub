package com.peoplehub.controller;
import com.peoplehub.model.Attendance;
import com.peoplehub.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.time.*;
import java.util.List;
@RestController @RequestMapping("/api/attendance")
public class AttendanceController {
 private final AttendanceRepository attendance; private final EmployeeRepository employees;
 public AttendanceController(AttendanceRepository a,EmployeeRepository e){attendance=a;employees=e;}
 @PostMapping("/check-in") public Attendance checkIn(Principal p){
  var emp=employees.findByUserUsername(p.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee profile not found"));
  var row=attendance.findByEmployeeIdAndWorkDate(emp.getId(),LocalDate.now()).orElseGet(()->new Attendance(emp,LocalDate.now()));
  if(row.getCheckIn()!=null) throw new ResponseStatusException(HttpStatus.CONFLICT,"Already checked in today");
  row.setCheckIn(LocalDateTime.now()); return attendance.save(row);
 }
 @PostMapping("/check-out") public Attendance checkOut(Principal p){
  var emp=employees.findByUserUsername(p.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee profile not found"));
  var row=attendance.findByEmployeeIdAndWorkDate(emp.getId(),LocalDate.now()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Check in first"));
  if(row.getCheckOut()!=null) throw new ResponseStatusException(HttpStatus.CONFLICT,"Already checked out");
  row.setCheckOut(LocalDateTime.now()); return attendance.save(row);
 }
 @GetMapping public List<Attendance> allOrMine(Principal p){
  boolean admin=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
  return admin?attendance.findAll():attendance.findByEmployeeUserUsernameOrderByWorkDateDesc(p.getName());
 }
 @GetMapping("/me") public List<Attendance> mine(Principal p){return attendance.findByEmployeeUserUsernameOrderByWorkDateDesc(p.getName());}
}
