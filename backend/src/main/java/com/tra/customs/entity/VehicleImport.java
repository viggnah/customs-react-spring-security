package com.tra.customs.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_imports")
public class VehicleImport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String vehicleId;
    
    @Column(nullable = false)
    private String make;
    
    @Column(nullable = false)
    private String model;
    
    @Column(nullable = false, name = "vehicle_year")
    private Integer year;
    
    @Column(name = "engine_number")
    private String engineNumber;
    
    @Column(name = "chassis_number")
    private String chassisNumber;
    
    @Column(nullable = false)
    private String origin;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal declaredValue;
    
    @Column(name = "engine_capacity", precision = 8, scale = 2)
    private BigDecimal engineCapacity;
    
    @Column(name = "fuel_type")
    private String fuelType;
    
    @Column
    private String color;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status = VehicleStatus.DOCUMENTATION_REVIEW;
    
    @Column(name = "submission_date")
    private LocalDateTime submissionDate;
    
    @Column(name = "inspection_date")
    private LocalDateTime inspectionDate;
    
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id")
    private User inspector;
    
    @Column(length = 2000)
    private String remarks;
    
    @Column(name = "duty_calculated", precision = 15, scale = 2)
    private BigDecimal dutyCalculated;
    
    @Column(name = "duty_paid", precision = 15, scale = 2)
    private BigDecimal dutyPaid;
    
    @Column(name = "registration_number")
    private String registrationNumber;
    
    // Constructors
    public VehicleImport() {}
    
    public VehicleImport(String vehicleId, String make, String model, Integer year, String origin, BigDecimal declaredValue) {
        this.vehicleId = vehicleId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.origin = origin;
        this.declaredValue = declaredValue;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public String getMake() {
        return make;
    }
    
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public String getEngineNumber() {
        return engineNumber;
    }
    
    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }
    
    public String getChassisNumber() {
        return chassisNumber;
    }
    
    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public BigDecimal getDeclaredValue() {
        return declaredValue;
    }
    
    public void setDeclaredValue(BigDecimal declaredValue) {
        this.declaredValue = declaredValue;
    }
    
    public BigDecimal getEngineCapacity() {
        return engineCapacity;
    }
    
    public void setEngineCapacity(BigDecimal engineCapacity) {
        this.engineCapacity = engineCapacity;
    }
    
    public String getFuelType() {
        return fuelType;
    }
    
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public VehicleStatus getStatus() {
        return status;
    }
    
    public void setStatus(VehicleStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }
    
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    public LocalDateTime getInspectionDate() {
        return inspectionDate;
    }
    
    public void setInspectionDate(LocalDateTime inspectionDate) {
        this.inspectionDate = inspectionDate;
    }
    
    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getInspector() {
        return inspector;
    }
    
    public void setInspector(User inspector) {
        this.inspector = inspector;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public BigDecimal getDutyCalculated() {
        return dutyCalculated;
    }
    
    public void setDutyCalculated(BigDecimal dutyCalculated) {
        this.dutyCalculated = dutyCalculated;
    }
    
    public BigDecimal getDutyPaid() {
        return dutyPaid;
    }
    
    public void setDutyPaid(BigDecimal dutyPaid) {
        this.dutyPaid = dutyPaid;
    }
    
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
