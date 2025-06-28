package com.tra.customs.dto;

import com.tra.customs.entity.CargoStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CargoEntryDto {
    private Long id;
    private String cargoId;
    private String description;
    private String origin;
    private String destination;
    private BigDecimal declaredValue;
    private BigDecimal weight;
    private BigDecimal volume;
    private String hsCode;
    private CargoStatus status;
    private LocalDateTime arrivalDate;
    private LocalDateTime inspectionDate;
    private LocalDateTime clearanceDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String inspectorName;
    private String remarks;
    private BigDecimal dutyCalculated;
    private BigDecimal dutyPaid;

    // Constructors
    public CargoEntryDto() {}

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

    public String getInspectorName() {
        return inspectorName;
    }

    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
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
}
