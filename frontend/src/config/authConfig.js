// WSO2 Identity Server Configuration
// TODO: Replace these with your actual WSO2 IS instance details

const authConfig = {
  // WSO2 Identity Server base URL
  // Example: https://localhost:9443 or https://your-is-instance.com
  baseUrl: process.env.REACT_APP_WSO2_BASE_URL || "https://localhost:9443",
  
  // OAuth2 Client Configuration
  clientID: process.env.REACT_APP_WSO2_CLIENT_ID || "YOUR_CLIENT_ID_HERE",

  clientSecret: process.env.REACT_APP_WSO2_CLIENT_SECRET || "YOUR_CLIENT_SECRET_HERE",

  // The URL to redirect to after login
  // This should be registered in your WSO2 IS application
  signInRedirectURL: process.env.REACT_APP_WSO2_SIGNIN_REDIRECT_URL || "http://localhost:3000",
  
  // The URL to redirect to after logout
  signOutRedirectURL: process.env.REACT_APP_WSO2_SIGNOUT_REDIRECT_URL || "http://localhost:3000",
  
  // OAuth2/OIDC scopes
  scope: ["openid", "profile", "email", "roles", "groups"],
  
  // Resource endpoints
  resourceServerURLs: [
    process.env.REACT_APP_API_BASE_URL || "http://localhost:8080"
  ],
  
  // PKCE (Proof Key for Code Exchange) - recommended for SPAs
  enablePKCE: true,
  
  // Session management
//   checkSessionInterval: 3000,
  
  // Custom claims mapping for TRA roles
//   customClaimsMapping: {
//     roles: "groups", // Map WSO2 IS groups to roles
//     authorities: "roles" // Map WSO2 IS roles to authorities
//   }
};

export default authConfig;
