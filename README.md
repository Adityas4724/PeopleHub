# PeopleHub — Employee Management System

A college-project MVP built with React, Spring Boot, Java 21 and MySQL.

## Included modules
- Admin and Employee sign-in using JWT
- Admin bootstrap from environment variables
- Department management (admin)
- Employee profile creation and listing (admin); employee can view own profile
- Attendance check-in/check-out and personal attendance history
- Leave request submission and admin approval/rejection
- Payroll record creation/listing (admin); employees can view their own payslips/records
- Role-based API authorization and BCrypt password hashing

> This is a functional educational MVP, not a production HR/payroll product. It does not yet include OTP activation, Gmail notifications, PDF payslips, audit trails, holiday calendars, or advanced reporting.

## Requirements
- Java 21
- Maven 3.9+
- Node.js 20+
- MySQL 8.x

## 1. Create the database
In MySQL Workbench, run:
```sql
CREATE DATABASE peoplehub_db;
```

## 2. Configure the backend
Open PowerShell in `backend` and set environment variables for this terminal:
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/peoplehub_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
$env:JWT_SECRET="UGVvcGxlSHViRGVtb1NlY3JldEtleUZvckpXVC0yMDI2"
$env:ADMIN_USERNAME="admin"
$env:ADMIN_EMAIL="admin@peoplehub.local"
$env:ADMIN_PASSWORD="ChangeThisAdmin123!"
mvn spring-boot:run
```
The admin account is created on first startup if it does not already exist. Change the sample admin password for your own use.

Backend: http://localhost:8080

## 3. Configure and run the frontend
Open a second PowerShell terminal in `frontend`:
```powershell
npm install
npm run dev
```
Frontend: http://localhost:5173

## Demo login
Use the `ADMIN_USERNAME` and `ADMIN_PASSWORD` values set above.

To create employee accounts, sign in as admin and use the Employees section. The admin assigns a temporary password; share it securely for a classroom demo and ask the employee to change it after login. For a real deployment, replace this with one-time activation links.

## API overview
- `POST /api/auth/login`
- `GET/POST /api/departments`
- `GET/POST /api/employees`
- `GET /api/employees/me`
- `POST /api/attendance/check-in`
- `POST /api/attendance/check-out`
- `GET /api/attendance/me`
- `POST /api/leaves`
- `GET /api/leaves`
- `PATCH /api/leaves/{id}/decision?status=APPROVED|REJECTED`
- `GET/POST /api/payroll`

All protected endpoints require `Authorization: Bearer <token>`.

## Notes
- Hibernate `ddl-auto=update` is enabled for local development.
- Do not commit real passwords or production JWT secrets.
- Use a long, random Base64-encoded secret for `JWT_SECRET` outside a classroom demo.
- For deployment, use HTTPS, database migrations, stronger account recovery, audit logging, and secure invitation flows.
