# Customs Management System

A comprehensive customs management application built with Spring Boot backend and React frontend for customs authority operations.

## 🏗️ Architecture
```
customs-react-spring-security/
├── backend/          # Spring Boot application
├── frontend/         # React application
├── docker/          # Docker configurations
└── docs/            # Documentation
```

## 🚀 Features

### Authentication & Authorization
- Spring Security with database authentication
- JWT token-based authentication
- Role-based access control (RBAC)

### Customs Operations
- **Cargo Clearance**: Manage cargo inspection and clearance processes
- **Vehicle Importation**: Handle vehicle import procedures and documentation
- **Duty Management**: Calculate and manage customs duties
- **Compliance Tracking**: Monitor regulatory compliance

### User Roles
- **ADMIN**: Full system access and user management
- **CUSTOMS_OFFICER**: General customs operations
- **CARGO_INSPECTOR**: Cargo-specific operations
- **VEHICLE_INSPECTOR**: Vehicle import operations
- **DUTY_OFFICER**: Duty calculation and management

## 🛠️ Technology Stack

### Backend
- Spring Boot 3.x
- Spring Security 6.x
- Spring Data JPA
- JWT Authentication
- Maven
- H2/PostgreSQL Database

### Frontend
- React 18.x
- React Router
- Axios
- CSS Modules
- Modern JavaScript (ES6+)

## 🚀 Quick Start

This application supports two authentication modes:

### Default Setup (Spring Security)
The main branch uses built-in Spring Security authentication with default users.

#### Prerequisites
- Java 17+
- Node.js 16+
- Maven 3.6+

#### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### Frontend Setup
```bash
cd frontend
npm install
npm start
```

### WSO2 Identity Server Integration
For enterprise identity management, switch to the WSO2 IS integration branch.

#### Prerequisites
- All default prerequisites above
- WSO2 Identity Server 7.1.0

#### Setup Steps
1. Switch to the feature branch:
   ```bash
   git checkout feature/wso2-identity-server-integration
   ```

2. Start WSO2 Identity Server and configure according to `BACKEND_WSO2_INTEGRATION.md`

3. Run backend with WSO2 profile:
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run -Dspring-boot.run.profiles=wso2
   ```

4. Run frontend:
   ```bash
   cd frontend
   npm install
   npm start
   ```

## 📊 Default Users (Spring Security setup)

| Username | Password | Role | Access Level |
|----------|----------|------|--------------|
| admin.customs | admin123 | ADMIN | Full access |
| john.smith | customs123 | CUSTOMS_OFFICER | General operations |
| jane.doe | cargo123 | CARGO_INSPECTOR | Cargo operations |
| mike.wilson | vehicle123 | VEHICLE_INSPECTOR | Vehicle operations |

## 📝 API Documentation

API documentation is available at: `http://localhost:8080/swagger-ui.html`

## 🐳 Docker Support
(TODO: Add Docker support for easy deployment)
```bash
# Build and run with Docker Compose
docker-compose up --build
```

## 📁 Project Structure

### Backend Structure
```
backend/src/main/java/com/customs/management/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── repository/      # Data repositories
├── service/         # Business logic
├── security/        # Security configuration
├── exception/       # Exception handling
└── util/           # Utility classes
```

### Frontend Structure
```
frontend/src/
├── components/      # Reusable components
├── pages/          # Page components
├── services/       # API services
├── context/        # React context
├── hooks/          # Custom hooks
├── utils/          # Utility functions
└── styles/         # CSS files
```
