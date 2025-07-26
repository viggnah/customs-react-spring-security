package com.customs.management.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cargo_entries")
public class CargoEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String cargoId;
    
    @Column(nullable = false, length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String origin;
    
    @Column(nullable = false)
    private String destination;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal declaredValue;
    
    @Column(precision = 10, scale = 3)
    private BigDecimal weight;
    
    @Column(precision = 10, scale = 3)
    private BigDecimal volume;
    
    @Column(length = 100)
    private String hsCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CargoStatus status = CargoStatus.PENDING_INSPECTION;
    
    @Column(name = "arrival_date")
    private LocalDateTime arrivalDate;
    
    @Column(name = "inspection_date")
    private LocalDateTime inspectionDate;
    
    @Column(name = "clearance_date")
    private LocalDateTime clearanceDate;
    
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
    
    // Constructors
    public CargoEntry() {}
    
    public CargoEntry(String cargoId, String description, String origin, String destination, BigDecimal declaredValue) {
        this.cargoId = cargoId;
        this.description = description;
        this.origin = origin;
        this.destination = destination;
        this.declaredValue = declaredValue;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCargoId() {
        return cargoId;
    }
    
    public void setCargoId(String cargoId) {
        this.cargoId = cargoId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public BigDecimal getDeclaredValue() {
        return declaredValue;
    }
    
    public void setDeclaredValue(BigDecimal declaredValue) {
        this.declaredValue = declaredValue;
    }
    
    public BigDecimal getWeight() {
        return weight;
    }
    
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    public BigDecimal getVolume() {
        return volume;
    }
    
    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
    
    public String getHsCode() {
        return hsCode;
    }
    
    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }
    
    public CargoStatus getStatus() {
        return status;
    }
    
    public void setStatus(CargoStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }
    
    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }
    
    public LocalDateTime getInspectionDate() {
        return inspectionDate;
    }
    
    public void setInspectionDate(LocalDateTime inspectionDate) {
        this.inspectionDate = inspectionDate;
    }
    
    public LocalDateTime getClearanceDate() {
        return clearanceDate;
    }
    
    public void setClearanceDate(LocalDateTime clearanceDate) {
        this.clearanceDate = clearanceDate;
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
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
