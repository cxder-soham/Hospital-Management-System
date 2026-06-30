# Spring Boot Data JPA & Security Hospital Management System

This is a comprehensive reference and education-focused project demonstrating **Spring Boot 3.5.3**, **Spring Data JPA**, and **Spring Security** (incorporating JWT and OAuth2 integration) applied to a real-world **Hospital Management System** domain.

---

## 🏗️ Core Features

- **Domain Entity Mappings**: Demonstrates diverse Hibernate relationship structures (One-to-One, One-to-Many, Many-to-Many), `@MapsId` shared keys, and indexes.
- **Advanced Spring Data JPA**: Shows derived query methods, custom `@Query` (JPQL and Native SQL), `@Modifying` transactional updates, pagination, sorting, DTO constructor projections, and N+1 query problem solutions via `LEFT JOIN FETCH`.
- **Hybrid Security Architecture**: Integrated JWT-based stateless authentication alongside social logins (Google, GitHub, and Twitter) using Spring Security OAuth2.
- **Method-Level Authorization**: Employs method annotations (`@Secured` and `@PreAuthorize`) linked with dynamically mapped roles and permissions.
- **Global Rest Exception Handler**: Standardizes error structures with `@RestControllerAdvice`.

---

## 🗄️ Domain Model & Database Schema

The domain model contains 6 principal entities situated under [entity]:

1. **[User.java]**: Implements Spring Security's `UserDetails`. Represents identity and holds roles.
2. **[Patient.java]**: Represents hospital patients. Shares its Primary Key with the `User` entity (`@MapsId`). Links to insurance and appointments.
3. **[Doctor.java]**: Represents medical staff. Shares its Primary Key with the `User` entity (`@MapsId`). Linked with departments and appointments.
4. **[Department.java]**: Represents specialized divisions (e.g. Cardiology, Orthopedics) managed by a head doctor.
5. **[Insurance.java]**: Holds coverage detail for a patient (1-to-1 mapping).
6. **[Appointment.java]**: Represents booked slots linking doctors and patients.

### Relationships

| Entity A | Entity B | Relationship Type | Notes |
| :--- | :--- | :--- | :--- |
| `Patient` | `User` | One-to-One | Primary key shared using `@MapsId` |
| `Doctor` | `User` | One-to-One | Primary key shared using `@MapsId` |
| `Patient` | `Insurance` | One-to-One | Owned by `Patient` via `patient_insurance_id` |
| `Patient` | `Appointment` | One-to-Many | Bidirectional; cascade on delete |
| `Doctor` | `Appointment` | One-to-Many | Bidirectional |
| `Department` | `Doctor` | Many-to-Many | Joint table `my_dpt_doctors` (`dpt_id`, `doctor_id`) |
| `Department` | `Doctor` | One-to-One | Points to the `headDoctor` |

---

## 🔒 Security Configuration

The security configuration resides in [security/WebSecurityConfig.java]:

- **JWT Authentication Filter**: Checks the `Authorization` header for `Bearer <JWT>` to authenticate request state. See [JwtAuthFilter.java].
- **OAuth2 Client Logging**: Authenticates social providers, maps attributes to internal `User`/`Patient` records, and sends JWT responses on success.
- **Roles and Authority Translation**: Dynamically maps roles (`PATIENT`, `DOCTOR`, `ADMIN`) into permissions (e.g., `PATIENT_READ`, `APPOINTMENT_WRITE`) in [RolePermissionMapping.java].

---

## 🛣️ API Endpoints Directory

All endpoints are prefixed with `/api/v1` (configured in `application.properties`).

### Public Endpoints (`/public/**`, `/auth/**`)
- **`POST /auth/signup`**: Create a new user profile as a Patient.
- **`POST /auth/login`**: Authenticate using username and password; returns JWT token.
- **`GET /public/doctors`**: Returns a list of all doctors.

### Authenticated Patient Endpoints (`/patients/**`)
- **`GET /patients/profile`**: Fetch current patient details.
- **`POST /patients/appointments`**: Book an appointment. Requires `ROLE_PATIENT`.

### Authenticated Doctor Endpoints (`/doctors/**`)
- **`GET /doctors/appointments`**: Get all appointments assigned to the logged-in doctor. Requires `ROLE_DOCTOR` or `ROLE_ADMIN`.

### Admin Endpoints (`/admin/**`)
- **`GET /admin/patients`**: Retrieve a paginated lists of patients (default size 10, page 0). Requires `ROLE_ADMIN`.
- **`POST /admin/onBoardNewDoctor`**: Promote an existing user to doctor status. Requires `ROLE_ADMIN`.
- **`DELETE /admin/**`**: Requires `APPOINTMENT_DELETE` or `USER_MANAGE` permissions.

---

## 💡 Advanced JPA Examples Used

### 1. JPQL Constructor Projection
In [PatientRepository.java]:
```java
@Query("select new com.flexi.soham.hospitalManagement.dto.BloodGroupCountResponseEntity(p.bloodGroup, Count(p)) from Patient p group by p.bloodGroup")
List<BloodGroupCountResponseEntity> countEachBloodGroupType();
```

### 2. N+1 Join Fetch
Resolving the N+1 SELECT problem when pulling patient records with their appointments:
```java
@Query("SELECT p FROM Patient p LEFT JOIN FETCH p.appointments")
List<Patient> findAllPatientWithAppointment();
```

### 3. Native Paginated Queries
Using Spring Data's `Pageable` on a raw SQL string:
```java
@Query(value = "select * from patient", nativeQuery = true)
Page<Patient> findAllPatients(Pageable pageable);
```

### 4. Modifying Queries
Making raw updates within transaction contexts:
```java
@Transactional
@Modifying
@Query("UPDATE Patient p SET p.name = :name where p.id = :id")
int updateNameWithId(@Param("name") String name, @Param("id") Long id);
```

---

## ⚙️ How to Get Started

### Prerequisites
- **Java Development Kit (JDK)**: Version 21
- **Database**: MySQL (configured on port `3306`) or PostgreSQL (dependency provided in POM)
- **Maven**: To build the application

### 1. Setup Application Database
Create a database named `hospitalDB` in your MySQL server:
```sql
CREATE DATABASE hospitalDB;
```

Update your connection credentials in [application.properties]:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospitalDB?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 2. Run the Application
You can run the application directly from the command line:
```bash
./mvnw spring-boot:run
```

Upon launching:
- Hibernate auto-generates tables based on annotations (`spring.jpa.hibernate.ddl-auto=create`).
- Initial data is populated from [data.sql] which sets up sample patients, doctors, and appointments.
