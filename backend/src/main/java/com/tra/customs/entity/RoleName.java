package com.tra.customs.entity;

public enum RoleName {
    ADMIN("Administrator with full system access"),
    CUSTOMS_OFFICER("General customs operations officer"),
    CARGO_INSPECTOR("Cargo inspection specialist"),
    VEHICLE_INSPECTOR("Vehicle importation specialist"),
    DUTY_OFFICER("Duty calculation and management specialist"),
    SUPERVISOR("Supervisory role with oversight capabilities");
    
    private final String description;
    
    RoleName(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
