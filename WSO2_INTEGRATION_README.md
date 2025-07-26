# WSO2 Identity Server Integration

This branch implements integration with WSO2 Identity Server using the Asgardeo Auth React SDK.

## Setup Instructions

### 1. Configure WSO2 Identity Server

You need to create an OAuth2/OIDC application in your WSO2 Identity Server. Here's what you need to configure:

#### Application Settings:
- **Application Type**: Single Page Application (SPA)
- **Grant Types**: Authorization Code with PKCE
- **Callback URLs**: 
  - `http://localhost:3000` (for development)
  - Your production URL (for production)
- **Logout URLs**: Same as callback URLs

#### Required Scopes:
- `openid`
- `profile` 
- `email`

#### Custom Claims (Optional):
You may want to configure custom claims to map user groups/roles:
- Groups claim: `groups`
- Roles claim: `roles`

### 2. Environment Configuration

1. Copy the environment template:
   ```bash
   cp .env.example .env
   ```

2. Update `.env` with your WSO2 IS configuration:
   ```env
   REACT_APP_WSO2_BASE_URL=https://your-wso2-is-domain.com
   REACT_APP_WSO2_CLIENT_ID=your_client_id_from_wso2_is
   REACT_APP_WSO2_SIGNIN_REDIRECT_URL=http://localhost:3000
   REACT_APP_WSO2_SIGNOUT_REDIRECT_URL=http://localhost:3000
   REACT_APP_API_BASE_URL=http://localhost:8080/api
   ```

### 3. User Group/Role Mapping

The application expects certain roles to be mapped from WSO2 IS groups. Configure these groups in WSO2 IS:

- `admin` → `ADMIN`
- `customs_officer` → `CUSTOMS_OFFICER`
- `cargo_inspector` → `CARGO_INSPECTOR`
- `vehicle_inspector` → `VEHICLE_INSPECTOR`
- `duty_officer` → `DUTY_OFFICER`

### 4. Backend Configuration

Update your Spring Boot backend to validate WSO2 IS tokens instead of generating custom JWTs.

## Architecture Changes

### Authentication Flow:
1. User clicks "Sign in with WSO2"
2. Redirected to WSO2 IS login page
3. After successful authentication, redirected back to React app
4. App receives authorization code and exchanges for tokens
5. Access token is used for API calls to Spring Boot backend
6. Backend validates tokens with WSO2 IS

### Key Components:

#### Frontend:
- `WSO2AuthContext.js`: New auth context using Asgardeo SDK
- `WSO2Login.js`: New login page with WSO2 authentication
- `wso2AuthService.js`: Service for authenticated API calls
- `authConfig.js`: WSO2 IS configuration

#### Backend (to be updated):
- Configure as OAuth2 Resource Server
- Remove custom JWT generation
- Add token validation with WSO2 IS

## Migration from Custom Auth

The integration maintains backward compatibility during transition:

1. **Dual Auth Support**: Both old and new auth contexts can coexist
2. **Gradual Migration**: Components can be updated one by one
3. **Fallback Login**: Legacy login available at `/legacy-login`

## Testing

1. Start WSO2 Identity Server
2. Configure the application as described above
3. Start the React app: `npm start`
4. Navigate to `http://localhost:3000`
5. Click "Sign in with WSO2" and test the flow

## Troubleshooting

### Common Issues:

1. **CORS Errors**: Ensure WSO2 IS allows your React app origin
2. **Redirect URI Mismatch**: Verify callback URLs are exactly registered in WSO2 IS
3. **Token Validation Errors**: Check that backend can reach WSO2 IS JWK endpoint
4. **Role Mapping Issues**: Verify group/role claims are properly configured

## Security Considerations

1. **PKCE**: Enabled by default for SPA security
2. **Token Storage**: Tokens are handled by Asgardeo SDK securely
3. **Automatic Refresh**: SDK handles token refresh automatically
4. **Session Management**: Proper logout and session cleanup

## Next Steps

1. Configure WSO2 IS application
2. Update environment variables
3. Test authentication flow
4. Update backend to validate WSO2 IS tokens
5. Remove legacy authentication code after testing
