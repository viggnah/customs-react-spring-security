package com.tra.customs.entity;

public enum AuthorityName {
    // User Management
    CREATE_USER("Create new users", "User Management"),
    READ_USER("View user information", "User Management"),
    UPDATE_USER("Update user details", "User Management"),
    DELETE_USER("Delete users", "User Management"),
    
    // Role Management
    MANAGE_ROLES("Manage user roles and permissions", "Role Management"),
    
    // Cargo Operations
    CREATE_CARGO("Register new cargo entries", "Cargo Operations"),
    READ_CARGO("View cargo information", "Cargo Operations"),
    UPDATE_CARGO("Update cargo details", "Cargo Operations"),
    DELETE_CARGO("Delete cargo entries", "Cargo Operations"),
    INSPECT_CARGO("Perform cargo inspections", "Cargo Operations"),
    APPROVE_CARGO("Approve cargo clearance", "Cargo Operations"),
    REJECT_CARGO("Reject cargo clearance", "Cargo Operations"),
    
    // Vehicle Operations
    CREATE_VEHICLE("Register vehicle imports", "Vehicle Operations"),
    READ_VEHICLE("View vehicle information", "Vehicle Operations"),
    UPDATE_VEHICLE("Update vehicle details", "Vehicle Operations"),
    DELETE_VEHICLE("Delete vehicle entries", "Vehicle Operations"),
    INSPECT_VEHICLE("Perform vehicle inspections", "Vehicle Operations"),
    APPROVE_VEHICLE("Approve vehicle import", "Vehicle Operations"),
    REJECT_VEHICLE("Reject vehicle import", "Vehicle Operations"),
    
    // Duty Management
    CALCULATE_DUTY("Calculate customs duties", "Duty Management"),
    APPROVE_DUTY("Approve duty calculations", "Duty Management"),
    PROCESS_PAYMENT("Process duty payments", "Duty Management"),
    REFUND_DUTY("Process duty refunds", "Duty Management"),
    
    // Reports and Analytics
    VIEW_REPORTS("View system reports", "Reports"),
    GENERATE_REPORTS("Generate custom reports", "Reports"),
    EXPORT_DATA("Export system data", "Reports"),
    
    // System Administration
    SYSTEM_CONFIG("Configure system settings", "System Administration"),
    VIEW_AUDIT_LOG("View system audit logs", "System Administration"),
    BACKUP_DATA("Perform data backups", "System Administration");
    
    private final String description;
    private final String category;
    
    AuthorityName(String description, String category) {
        this.description = description;
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getCategory() {
        return category;
    }
}
