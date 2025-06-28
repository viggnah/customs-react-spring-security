# Backend WSO2 Identity Server Integration

This document describes the backend changes required to integrate with WSO2 Identity Server.

## Changes Made

### 1. Dependencies Added
- `spring-boot-starter-oauth2-resource-server`: OAuth2 Resource Server support
- Enables JWT token validation with WSO2 IS

### 2. Security Configuration
- **WSO2SecurityConfig.java**: New security configuration for WSO2 IS integration
- **Replaces**: WebSecurityConfig.java (for WSO2 profile)
- **Features**:
  - JWT token validation using WSO2 IS JWKS endpoint
  - Automatic mapping of WSO2 IS groups to Spring Security authorities
  - Role-based access control for API endpoints

### 3. Group to Authority Mapping
```java
Map<String, List<String>> groupToAuthorities = Map.of(
    "admin", List.of("ROLE_ADMIN", "READ", "WRITE", "DELETE", "MANAGE_USERS", "VIEW_REPORTS"),
    "customs_officer", List.of("ROLE_CUSTOMS_OFFICER", "READ", "WRITE", "PROCESS_CLEARANCE", "VIEW_REPORTS"),
    "cargo_inspector", List.of("ROLE_CARGO_INSPECTOR", "READ", "WRITE", "INSPECT_CARGO"),
    "vehicle_inspector", List.of("ROLE_VEHICLE_INSPECTOR", "READ", "WRITE", "INSPECT_VEHICLE"),
    "duty_officer", List.of("ROLE_DUTY_OFFICER", "READ", "WRITE", "CALCULATE_DUTY", "PROCESS_PAYMENT")
);
```

### 4. New Endpoints
- `POST /api/auth/validate`: Validates WSO2 IS token and returns user info
- `GET /api/auth/user-info`: Returns detailed user information
- `POST /api/auth/refresh`: Validates current token status

### 5. Services
- **WSO2UserService**: Extracts and processes user information from JWT tokens
- **Features**:
  - Flexible username extraction (tries multiple JWT claims)
  - Role and authority management
  - User information mapping

## Configuration

### Environment Variables

Create a `.env` file in the backend directory:

```properties
# WSO2 Identity Server Configuration
WSO2_ISSUER_URI=https://localhost:9443/oauth2/token
WSO2_JWK_SET_URI=https://localhost:9443/oauth2/jwks
WSO2_BASE_URL=https://localhost:9443
WSO2_CLIENT_ID=your_client_id
WSO2_CLIENT_SECRET=your_client_secret

# SSL Configuration (Development only)
WSO2_TRUST_ALL_SSL=true
WSO2_VERIFY_HOSTNAME=false
```

### Profile Configuration

Use the WSO2 profile to enable WSO2 IS integration:

```bash
# Run with WSO2 IS integration
java -jar target/customs-backend-1.0.0.jar --spring.profiles.active=wso2

# Or set environment variable
export SPRING_PROFILES_ACTIVE=wso2
mvn spring-boot:run
```

### WSO2 IS Setup Requirements

1. **OAuth2 Application in WSO2 IS**:
   - Application Type: Single Page Application (SPA)
   - Grant Types: Authorization Code with PKCE
   - Allowed Origins: `http://localhost:3000`, `http://localhost:8080`

2. **Required Scopes**:
   - `openid`
   - `profile`
   - `email`

3. **User Groups** (configure in WSO2 IS):
   - `admin`
   - `customs_officer`
   - `cargo_inspector`
   - `vehicle_inspector`
   - `duty_officer`

4. **Claims Configuration**:
   - Ensure `groups` claim is included in ID and Access tokens
   - Configure user attributes: `given_name`, `family_name`, `email`

## API Endpoint Protection

### New Security Rules:
```java
.authorizeHttpRequests(authz -> authz
    // Public endpoints
    .requestMatchers("/api/auth/validate", "/api/auth/refresh").permitAll()
    
    // Admin endpoints
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    
    // Cargo endpoints
    .requestMatchers("/api/cargo/**").hasAnyAuthority(
        "READ", "WRITE", "INSPECT_CARGO", "PROCESS_CLEARANCE"
    )
    
    // Other endpoints...
    .anyRequest().authenticated()
);
```

## Testing

### 1. Start WSO2 Identity Server
```bash
# Default WSO2 IS startup
./wso2server.sh
```

### 2. Start Backend with WSO2 Profile
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=wso2
```

### 3. Test Endpoints
```bash
# Get a token from frontend or WSO2 IS directly
# Then test validation endpoint

curl -X POST http://localhost:8080/api/auth/validate \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

## Migration Strategy

### Option 1: Parallel Deployment
- Keep both `WebSecurityConfig` and `WSO2SecurityConfig`
- Use profiles to switch between them
- Test thoroughly before removing legacy config

### Option 2: Gradual Migration
1. Deploy with WSO2 profile for testing
2. Validate all endpoints work correctly
3. Update frontend to use WSO2 authentication
4. Remove legacy authentication code

## Troubleshooting

### Common Issues:

1. **SSL Certificate Issues**:
   ```properties
   # For development only
   WSO2_TRUST_ALL_SSL=true
   WSO2_VERIFY_HOSTNAME=false
   ```

2. **JWKS Endpoint Unreachable**:
   - Check WSO2 IS is running
   - Verify network connectivity
   - Check firewall settings

3. **Token Validation Fails**:
   - Verify token format (should be JWT)
   - Check token expiration
   - Ensure correct issuer and audience

4. **Group Mapping Issues**:
   - Verify groups are included in JWT claims
   - Check group names match exactly
   - Review WSO2 IS claims configuration

### Debug Logging:
```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.oauth2=DEBUG
```

## Production Considerations

1. **SSL Certificates**: Use proper SSL certificates for WSO2 IS
2. **Network Security**: Secure communication between services
3. **Token Validation**: Configure appropriate cache settings
4. **Monitoring**: Add metrics for authentication success/failure
5. **High Availability**: Consider WSO2 IS clustering for production
