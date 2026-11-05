package com.peoplehub.controller;
import com.peoplehub.dto.LeaveRequestDto;
import com.peoplehub.model.*;
import com.peoplehub.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.util.List;
@RestController @RequestMapping("/api/leaves")
public class LeaveController {
 private final LeaveRequestRepository leaves; private final EmployeeRepository employees;
 public LeaveController(LeaveRequestRepository l,EmployeeRepository e){leaves=l;employees=e;}
 @PostMapping public LeaveRequest apply(Principal p,@Valid @RequestBody LeaveRequestDto r){
  if(r.endDate().isBefore(r.startDate())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"End date must be on or after start date");
  var emp=employees.findByUserUsername(p.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee profile not found"));
  return leaves.save(new LeaveRequest(emp,r.startDate(),r.endDate(),r.reason()));
 }
 @GetMapping public List<LeaveRequest> allOrMine(Principal p){
  boolean admin=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
  return admin?leaves.findAll():leaves.findByEmployeeUserUsernameOrderByCreatedAtDesc(p.getName());
 }
 @PatchMapping("/{id}/decision") @PreAuthorize("hasRole('ADMIN')") public LeaveRequest decide(@PathVariable Long id,@RequestParam LeaveStatus status){
  if(status==LeaveStatus.PENDING) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Decision must be APPROVED or REJECTED");
  var row=leaves.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Leave request not found"));
  row.setStatus(status);return leaves.save(row);
 }
}
