package com.customs.management.controller;

import com.customs.management.dto.CargoEntryDto;
import com.customs.management.dto.CreateCargoEntryRequest;
import com.customs.management.dto.UpdateCargoEntryRequest;
import com.customs.management.entity.CargoStatus;
import com.customs.management.service.CargoService;
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
@RequestMapping("/cargo")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CargoController {

    @Autowired
    private CargoService cargoService;

    @GetMapping
    @PreAuthorize("hasAuthority('READ_CARGO')")
    public ResponseEntity<Page<CargoEntryDto>> getAllCargo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) CargoStatus status) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CargoEntryDto> cargoPage;
        if (status != null) {
            cargoPage = cargoService.getCargoByStatus(status, pageable);
        } else {
            cargoPage = cargoService.getAllCargo(pageable);
        }
        
        return ResponseEntity.ok(cargoPage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_CARGO')")
    public ResponseEntity<CargoEntryDto> getCargoById(@PathVariable Long id) {
        Optional<CargoEntryDto> cargo = cargoService.getCargoById(id);
        return cargo.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cargo-id/{cargoId}")
    @PreAuthorize("hasAuthority('READ_CARGO')")
    public ResponseEntity<CargoEntryDto> getCargoByCargoId(@PathVariable String cargoId) {
        Optional<CargoEntryDto> cargo = cargoService.getCargoByCargoId(cargoId);
        return cargo.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_CARGO')")
    public ResponseEntity<CargoEntryDto> createCargo(@Valid @RequestBody CreateCargoEntryRequest request) {
        try {
            CargoEntryDto createdCargo = cargoService.createCargo(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCargo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CARGO')")
    public ResponseEntity<CargoEntryDto> updateCargo(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateCargoEntryRequest request) {
        Optional<CargoEntryDto> updatedCargo = cargoService.updateCargo(id, request);
        return updatedCargo.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{cargoId}/assign-inspector/{inspectorId}")
    @PreAuthorize("hasAuthority('INSPECT_CARGO')")
    public ResponseEntity<CargoEntryDto> assignInspector(
            @PathVariable Long cargoId, 
            @PathVariable Long inspectorId) {
        Optional<CargoEntryDto> updatedCargo = cargoService.assignInspector(cargoId, inspectorId);
        return updatedCargo.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{cargoId}/calculate-duty")
    @PreAuthorize("hasAuthority('CALCULATE_DUTY')")
    public ResponseEntity<CargoEntryDto> calculateDuty(
            @PathVariable Long cargoId, 
            @RequestParam BigDecimal dutyAmount) {
        Optional<CargoEntryDto> updatedCargo = cargoService.calculateDuty(cargoId, dutyAmount);
        return updatedCargo.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{cargoId}/record-payment")
    @PreAuthorize("hasAuthority('PROCESS_PAYMENT')")
    public ResponseEntity<CargoEntryDto> recordDutyPayment(
            @PathVariable Long cargoId, 
            @RequestParam BigDecimal paidAmount) {
        Optional<CargoEntryDto> updatedCargo = cargoService.recordDutyPayment(cargoId, paidAmount);
        return updatedCargo.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_CARGO')")
    public ResponseEntity<?> deleteCargo(@PathVariable Long id) {
        boolean deleted = cargoService.deleteCargo(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
