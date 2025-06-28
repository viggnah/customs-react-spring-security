package com.tra.customs.entity;

public enum VehicleStatus {
    DOCUMENTATION_REVIEW("Documentation under review"),
    PENDING_INSPECTION("Waiting for physical inspection"),
    UNDER_INSPECTION("Currently being inspected"),
    INSPECTION_COMPLETED("Inspection completed, awaiting approval"),
    APPROVED("Vehicle import approved"),
    REJECTED("Vehicle import rejected"),
    DUTY_PENDING("Waiting for duty payment"),
    DUTY_PAID("Duty payment completed"),
    REGISTERED("Vehicle registered and ready for use");
    
    private final String description;
    
    VehicleStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
