package com.customs.management.controller;

import com.customs.management.dto.VehicleImportDto;
import com.customs.management.dto.CreateVehicleImportRequest;
import com.customs.management.entity.VehicleStatus;
import com.customs.management.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    @PreAuthorize("hasAuthority('READ_VEHICLE')")
    public ResponseEntity<Page<VehicleImportDto>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) VehicleStatus status) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<VehicleImportDto> vehiclePage;
        if (status != null) {
            vehiclePage = vehicleService.getVehiclesByStatus(status, pageable);
        } else {
            vehiclePage = vehicleService.getAllVehicles(pageable);
        }
        
        return ResponseEntity.ok(vehiclePage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_VEHICLE')")
    public ResponseEntity<VehicleImportDto> getVehicleById(@PathVariable Long id) {
        Optional<VehicleImportDto> vehicle = vehicleService.getVehicleById(id);
        return vehicle.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/vehicle-id/{vehicleId}")
    @PreAuthorize("hasAuthority('READ_VEHICLE')")
    public ResponseEntity<VehicleImportDto> getVehicleByVehicleId(@PathVariable String vehicleId) {
        Optional<VehicleImportDto> vehicle = vehicleService.getVehicleByVehicleId(vehicleId);
        return vehicle.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_VEHICLE')")
    public ResponseEntity<VehicleImportDto> createVehicleImport(@Valid @RequestBody CreateVehicleImportRequest request) {
        try {
            VehicleImportDto createdVehicle = vehicleService.createVehicleImport(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('UPDATE_VEHICLE')")
    public ResponseEntity<VehicleImportDto> updateVehicleStatus(
            @PathVariable Long id, 
            @RequestParam VehicleStatus status) {
        Optional<VehicleImportDto> updatedVehicle = vehicleService.updateVehicleStatus(id, status);
        return updatedVehicle.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{vehicleId}/assign-inspector/{inspectorId}")
    @PreAuthorize("hasAuthority('INSPECT_VEHICLE')")
    public ResponseEntity<VehicleImportDto> assignInspector(
            @PathVariable Long vehicleId, 
            @PathVariable Long inspectorId) {
        Optional<VehicleImportDto> updatedVehicle = vehicleService.assignInspector(vehicleId, inspectorId);
        return updatedVehicle.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{vehicleId}/calculate-duty")
    @PreAuthorize("hasAuthority('CALCULATE_DUTY')")
    public ResponseEntity<VehicleImportDto> calculateDuty(
            @PathVariable Long vehicleId, 
            @RequestParam BigDecimal dutyAmount) {
        Optional<VehicleImportDto> updatedVehicle = vehicleService.calculateDuty(vehicleId, dutyAmount);
        return updatedVehicle.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{vehicleId}/record-payment")
    @PreAuthorize("hasAuthority('PROCESS_PAYMENT')")
    public ResponseEntity<VehicleImportDto> recordDutyPayment(
            @PathVariable Long vehicleId, 
            @RequestParam BigDecimal paidAmount) {
        Optional<VehicleImportDto> updatedVehicle = vehicleService.recordDutyPayment(vehicleId, paidAmount);
        return updatedVehicle.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{vehicleId}/registration")
    @PreAuthorize("hasAuthority('UPDATE_VEHICLE')")
    public ResponseEntity<VehicleImportDto> assignRegistrationNumber(
            @PathVariable Long vehicleId, 
            @RequestParam String registrationNumber) {
        Optional<VehicleImportDto> updatedVehicle = vehicleService.assignRegistrationNumber(vehicleId, registrationNumber);
        return updatedVehicle.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_VEHICLE')")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        boolean deleted = vehicleService.deleteVehicle(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
