package com.peoplehub.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
public class Payroll {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private Employee employee;
 @Column(nullable=false) private String payMonth;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal basic;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal allowances;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal deductions;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal netPay;
 private LocalDate paidOn;
 protected Payroll() {}
 public Payroll(Employee e,String month,BigDecimal basic,BigDecimal allowances,BigDecimal deductions,LocalDate paidOn){
  this.employee=e;this.payMonth=month;this.basic=basic;this.allowances=allowances;this.deductions=deductions;
  this.netPay=basic.add(allowances).subtract(deductions);this.paidOn=paidOn;
 }
 public Long getId(){return id;} public Employee getEmployee(){return employee;} public String getPayMonth(){return payMonth;}
 public BigDecimal getBasic(){return basic;} public BigDecimal getAllowances(){return allowances;} public BigDecimal getDeductions(){return deductions;}
 public BigDecimal getNetPay(){return netPay;} public LocalDate getPaidOn(){return paidOn;}
}
