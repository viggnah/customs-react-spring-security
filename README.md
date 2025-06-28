# TRA Customs Management System

A comprehensive customs management application built with Spring Boot backend and React frontend for tax authority operations.

## 🏗️ Architecture

```
tra-customs-spring/
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
- Password reset functionality

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

## 🎨 Design Theme
- **Primary Colors**: Bright Yellow (#FFD700) and Black (#000000)
- **Typography**: Professional and accessible
- **UI/UX**: Modern, responsive design optimized for government operations

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 16+
- Maven 3.6+

### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm start
```

## 📊 Default Users

| Username | Password | Role | Access Level |
|----------|----------|------|--------------|
| admin.tra | admin123 | ADMIN | Full access |
| john.smith | customs123 | CUSTOMS_OFFICER | General operations |
| jane.doe | cargo123 | CARGO_INSPECTOR | Cargo operations |
| mike.wilson | vehicle123 | VEHICLE_INSPECTOR | Vehicle operations |

## 🔧 Configuration

The application supports multiple environments through Spring profiles:
- `dev` - Development with H2 database
- `staging` - Staging environment
- `prod` - Production with PostgreSQL

## 📝 API Documentation

API documentation is available at: `http://localhost:8080/swagger-ui.html`

## 🐳 Docker Support

```bash
# Build and run with Docker Compose
docker-compose up --build
```

## 🔐 Security Features

- Password encryption using BCrypt
- JWT token expiration and refresh
- CORS configuration for frontend integration
- SQL injection prevention
- XSS protection headers

## 📁 Project Structure

### Backend Structure
```
backend/src/main/java/com/tra/customs/
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

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Run tests
4. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions, please contact the development team.
