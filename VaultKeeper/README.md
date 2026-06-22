# VaultKeeper - Secure Vault Application

## Project Title and Description

**VaultKeeper** is a secure web application that allows users to store, retrieve, update, and delete private words or codes linked to different platforms (bank PIN hints, Wi-Fi passphrases, email recovery codes, app unlock patterns) all in one place, secured behind authentication.

### Key Features
-  Secure authentication with BCrypt password hashing
-  Complete CRUD operations for vault entries
-  Secret masking (only last 2 characters visible)
-  Cookie-based last platform memory (7 days expiry)
-  Search/filter by platform name
-  Session management with proper logout
-  Responsive, user-friendly interface


## Technology Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 3.1.5 |
| MVC | Spring MVC with Thymeleaf |
| Security | Spring Security (BCrypt) |
| Database | MySQL |
| ORM | Spring Data JPA (Hibernate) |
| Build Tool | Maven |
| Java Version | JDK 17+ |
| Server | Embedded Tomcat |

---

## Architecture Justification

### Framework Components Selected

1. **Spring Boot** - Provides auto-configuration, embedded Tomcat, and production-ready features
2. **Spring MVC** - Implements proper Model-View-Controller separation
3. **Thymeleaf** - Server-side template engine with natural templating
4. **Spring Security** - Handles authentication, BCrypt password encoding, session management
5. **Spring Data JPA** - Simplifies database operations with repository pattern
6. **MySQL** - Relational database for persistent storage

### Why This Architecture?

The chosen architecture aligns with exam requirements:
- **MVC Separation**: Controllers handle requests, Models hold data, Views (Thymeleaf) handle presentation only
- **No business logic in templates**: All Java logic remains in controllers/services
- **Session Management**: Multiple techniques implemented (see below)
- **Security**: BCrypt hashing, HttpOnly cookies, session invalidation on logout

---

## Session Management Techniques (4 Required Techniques)

| # | Technique | Implementation Location | Purpose |
|---|-----------|------------------------|---------|
| 1 | **@SessionAttributes** | `VaultController` line 29 | Stores username across multiple requests |
| 2 | **HttpSession** | `VaultController` lines 54, 102, 118 | Stores last platform name in session |
| 3 | **Cookie (HttpOnly + Secure)** | `VaultController` lines 94-100 | Pre-fills platform name for 7 days |
| 4 | **@PathVariable** | `VaultController` lines 124, 146, 172 | Passes entry ID via URL |
| 5 | **Hidden Form Field** | `edit-entry.html` line 91 | Carries entry ID without user typing |

> All four techniques from the exam specification are present and functionally meaningful.

---

## Setup Instructions

### Prerequisites

- JDK 17 or higher
- MySQL Server (8.0+ recommended)
- Maven (or use included Maven wrapper)

### Database Setup

1. **Start MySQL Server**

2. **Create the database:**
```sql
CREATE DATABASE vaultkeeper;
