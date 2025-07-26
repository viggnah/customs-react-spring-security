package com.customs.management.entity;

public enum CargoStatus {
    PENDING_INSPECTION("Waiting for initial inspection"),
    UNDER_INSPECTION("Currently being inspected"),
    INSPECTION_COMPLETED("Inspection completed, awaiting clearance"),
    CLEARED("Cargo cleared for delivery"),
    HELD("Cargo held for further investigation"),
    REJECTED("Cargo rejected and not cleared"),
    DUTY_PENDING("Waiting for duty payment"),
    DUTY_PAID("Duty payment completed");
    
    private final String description;
    
    CargoStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
