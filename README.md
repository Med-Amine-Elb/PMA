# 📞 TelephoneManager Backend

[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-brightgreen.svg)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A robust Spring Boot backend for managing corporate phone and SIM card assignments, user roles, and inventory. Designed for IT departments and asset managers to streamline device and SIM allocation, tracking, and reporting.

---

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- SQL Server 2019 or higher
- Postman (for API testing)

### Database Setup
1. **Install SQL Server** ([Download](https://www.microsoft.com/en-us/sql-server/sql-server-downloads))
2. **Create Database**
   ```sql
   CREATE DATABASE TelephoneManager;
   ```
3. **Configure Connection**
   - Edit `src/main/resources/application.yml`:
     ```yaml
     spring:
       datasource:
         url: jdbc:sqlserver://localhost:1433;databaseName=TelephoneManager;encrypt=true;trustServerCertificate=true
         username: sa
         password: YourStrong@Passw0rd
     ```

### Build & Run
```bash
mvn clean install
mvn spring-boot:run
```
- App runs at: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)

---

## 🧪 API Testing with Postman
- Import: `postman/TelephoneManager_API.postman_collection.json`
- Set environment variables:
  - `baseUrl`: `http://localhost:8080/api`
  - `authToken`: (auto-set after login)

### Test Users
| Email                | Password   | Role     |
|----------------------|------------|----------|
| admin@company.com    | admin123   | ADMIN    |
| assigner@company.com | assigner123| ASSIGNER |
| john@company.com     | user123    | USER     |

---

## 📋 Features
- **Authentication**: JWT, role-based (ADMIN, ASSIGNER, USER)
- **User Management**: CRUD, search, filtering
- **Phone Management**: Inventory, status, usage
- **SIM Card Management**: Inventory, carrier, assignment
- **Audit Logging**: Track changes and assignments
- **API Documentation**: Swagger/OpenAPI

---

## 🏗️ Project Structure
```text
src/main/java/com/telephonemanager/
├── config/         # Configuration classes
├── controller/     # REST controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── repository/     # Data access layer
├── security/       # Security config
└── service/        # Business logic

src/main/resources/
├── application.yml # App config
└── data.sql        # Initial data (optional)

postman/
└── TelephoneManager_API.postman_collection.json
```

---

## 🔧 Configuration
- **JWT**: Set secret and expiration in `application.yml`
- **Database**: Configure connection string and credentials

---

## 🐛 Troubleshooting
- **DB Connection**: Check SQL Server status and config
- **JWT Issues**: Verify secret and token format
- **Port Conflicts**: Change port in `application.yml` or free port 8080
- **Logs**: See `logs/application.log` for details

---

## 🤝 Contributing
1. Fork & branch
2. Implement changes
3. Add tests
4. Open a pull request

---

## 📄 License
MIT License

---

> _Developed for efficient device and SIM management in modern organizations._ 
