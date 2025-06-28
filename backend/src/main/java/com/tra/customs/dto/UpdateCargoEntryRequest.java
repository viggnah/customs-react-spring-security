package com.tra.customs.dto;

import com.tra.customs.entity.CargoStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UpdateCargoEntryRequest {
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private String origin;
    
    private String destination;
    
    @DecimalMin(value = "0.01", message = "Declared value must be greater than 0")
    private BigDecimal declaredValue;
    
    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    private BigDecimal weight;
    
    @DecimalMin(value = "0.0", message = "Volume must be non-negative")
    private BigDecimal volume;
    
    @Size(max = 100, message = "HS Code must not exceed 100 characters")
    private String hsCode;
    
    private CargoStatus status;
    
    private LocalDateTime arrivalDate;
    
    private LocalDateTime inspectionDate;
    
    private LocalDateTime clearanceDate;
    
    private String remarks;
    
    @DecimalMin(value = "0.0", message = "Duty calculated must be non-negative")
    private BigDecimal dutyCalculated;
    
    @DecimalMin(value = "0.0", message = "Duty paid must be non-negative")
    private BigDecimal dutyPaid;

    // Constructors
    public UpdateCargoEntryRequest() {}

    // Getters and Setters
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
