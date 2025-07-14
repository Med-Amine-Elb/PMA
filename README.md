# TelephoneManager Backend

A Spring Boot application for managing corporate phone and SIM card assignments.

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- SQL Server 2019 or higher
- Postman (for API testing)

### Database Setup

1. **Install SQL Server** (if not already installed)
   - Download from: https://www.microsoft.com/en-us/sql-server/sql-server-downloads
   - Install with default settings

2. **Create Database**
   ```sql
   CREATE DATABASE TelephoneManager;
   ```

3. **Update Configuration**
   - Edit `src/main/resources/application.yml`
   - Update database connection details:
     ```yaml
     spring:
       datasource:
         url: jdbc:sqlserver://localhost:1433;databaseName=TelephoneManager;encrypt=true;trustServerCertificate=true
         username: sa
         password: YourStrong@Passw0rd
     ```

### Running the Application

1. **Clone and Navigate**
   ```bash
   cd telephone-manager-backend
   ```

2. **Build the Project**
   ```bash
   mvn clean install
   ```

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

4. **Verify Startup**
   - Application will start on `http://localhost:8080`
   - Swagger UI available at: `http://localhost:8080/api/swagger-ui.html`
   - API Documentation at: `http://localhost:8080/api/api-docs`

## 🧪 Testing with Postman

### Import Collection

1. Open Postman
2. Import the collection: `postman/TelephoneManager_API.postman_collection.json`
3. Create an environment with variables:
   - `baseUrl`: `http://localhost:8080/api`
   - `authToken`: (will be set automatically after login)

### Test Users

The application creates test users automatically:

| Email | Password | Role |
|-------|----------|------|
| admin@company.com | admin123 | ADMIN |
| assigner@company.com | assigner123 | ASSIGNER |
| john@company.com | user123 | USER |

### Testing Steps

1. **Login Test**
   - Use the "Login" request in the Authentication folder
   - Try with different user credentials
   - Verify the token is saved automatically

2. **Get Current User**
   - Use the "Get Current User" request
   - Should return user information based on the token

3. **Logout Test**
   - Use the "Logout" request
   - Verify successful logout

## 📋 Current Implementation

### ✅ Completed Features

- **Authentication System**
  - JWT-based authentication
  - Role-based access control (ADMIN, ASSIGNER, USER)
  - Login/Logout endpoints
  - Get current user information

### 🔄 Next Steps

1. **User Management** (Phase 2)
   - CRUD operations for users
   - Role-based access control
   - User search and filtering

2. **Phone Management** (Phase 3)
   - Phone inventory management
   - Status tracking
   - Usage statistics

3. **SIM Card Management** (Phase 4)
   - SIM card inventory
   - Carrier management
   - Assignment tracking

## 🏗️ Project Structure

```
src/main/java/com/telephonemanager/
├── config/                 # Configuration classes
├── controller/            # REST controllers
├── dto/                   # Data Transfer Objects
├── entity/                # JPA entities
├── repository/            # Data access layer
├── security/              # Security configuration
└── service/               # Business logic

src/main/resources/
├── application.yml        # Application configuration
└── data.sql              # Initial data (if needed)

postman/
└── TelephoneManager_API.postman_collection.json
```

## 🔧 Configuration

### JWT Configuration
```yaml
jwt:
  secret: your-256-bit-secret-key-here-make-it-very-long-and-secure
  expiration: 86400000 # 24 hours
```

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=TelephoneManager
    username: sa
    password: YourStrong@Passw0rd
```

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify SQL Server is running
   - Check connection string in `application.yml`
   - Ensure database exists

2. **JWT Token Issues**
   - Check JWT secret in configuration
   - Verify token format in Authorization header

3. **Port Already in Use**
   - Change port in `application.yml`
   - Or kill process using port 8080

### Logs

Check application logs for detailed error information:
```bash
tail -f logs/application.log
```

## 📚 API Documentation

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api/api-docs

## 🤝 Contributing

1. Create a feature branch
2. Implement your changes
3. Add tests
4. Submit a pull request

## 📄 License

This project is licensed under the MIT License. 