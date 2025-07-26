package com.customs.management.service;

import com.customs.management.dto.VehicleImportDto;
import com.customs.management.dto.CreateVehicleImportRequest;
import com.customs.management.entity.VehicleImport;
import com.customs.management.entity.VehicleStatus;
import com.customs.management.entity.User;
import com.customs.management.repository.VehicleImportRepository;
import com.customs.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class VehicleService {

    @Autowired
    private VehicleImportRepository vehicleImportRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<VehicleImportDto> getAllVehicles(Pageable pageable) {
        return vehicleImportRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    public Page<VehicleImportDto> getVehiclesByStatus(VehicleStatus status, Pageable pageable) {
        return vehicleImportRepository.findByStatus(status, pageable)
                .map(this::convertToDto);
    }

    public Optional<VehicleImportDto> getVehicleById(Long id) {
        return vehicleImportRepository.findById(id)
                .map(this::convertToDto);
    }

    public Optional<VehicleImportDto> getVehicleByVehicleId(String vehicleId) {
        return vehicleImportRepository.findByVehicleId(vehicleId)
                .map(this::convertToDto);
    }

    public VehicleImportDto createVehicleImport(CreateVehicleImportRequest request) {
        // Check if vehicle ID already exists
        if (vehicleImportRepository.findByVehicleId(request.getVehicleId()).isPresent()) {
            throw new IllegalArgumentException("Vehicle ID already exists: " + request.getVehicleId());
        }

        VehicleImport vehicle = new VehicleImport();
        vehicle.setVehicleId(request.getVehicleId());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setEngineNumber(request.getEngineNumber());
        vehicle.setChassisNumber(request.getChassisNumber());
        vehicle.setOrigin(request.getOrigin());
        vehicle.setDeclaredValue(request.getDeclaredValue());
        vehicle.setEngineCapacity(request.getEngineCapacity());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setColor(request.getColor());
        vehicle.setSubmissionDate(request.getSubmissionDate());
        vehicle.setRemarks(request.getRemarks());
        vehicle.setStatus(VehicleStatus.DOCUMENTATION_REVIEW);

        VehicleImport savedVehicle = vehicleImportRepository.save(vehicle);
        return convertToDto(savedVehicle);
    }

    public Optional<VehicleImportDto> updateVehicleStatus(Long id, VehicleStatus status) {
        return vehicleImportRepository.findById(id)
                .map(vehicle -> {
                    vehicle.setStatus(status);
                    
                    // Set appropriate dates based on status
                    switch (status) {
                        case UNDER_INSPECTION:
                            vehicle.setInspectionDate(LocalDateTime.now());
                            break;
                        case APPROVED:
                            vehicle.setApprovalDate(LocalDateTime.now());
                            break;
                        default:
                            break;
                    }
                    
                    VehicleImport updatedVehicle = vehicleImportRepository.save(vehicle);
                    return convertToDto(updatedVehicle);
                });
    }

    public Optional<VehicleImportDto> assignInspector(Long vehicleId, Long inspectorId) {
        Optional<VehicleImport> vehicleOpt = vehicleImportRepository.findById(vehicleId);
        Optional<User> inspectorOpt = userRepository.findById(inspectorId);

        if (vehicleOpt.isPresent() && inspectorOpt.isPresent()) {
            VehicleImport vehicle = vehicleOpt.get();
            User inspector = inspectorOpt.get();
            
            vehicle.setInspector(inspector);
            vehicle.setStatus(VehicleStatus.UNDER_INSPECTION);
            vehicle.setInspectionDate(LocalDateTime.now());
            
            VehicleImport updatedVehicle = vehicleImportRepository.save(vehicle);
            return Optional.of(convertToDto(updatedVehicle));
        }
        
        return Optional.empty();
    }

    public Optional<VehicleImportDto> calculateDuty(Long vehicleId, BigDecimal dutyAmount) {
        return vehicleImportRepository.findById(vehicleId)
                .map(vehicle -> {
                    vehicle.setDutyCalculated(dutyAmount);
                    VehicleImport updatedVehicle = vehicleImportRepository.save(vehicle);
                    return convertToDto(updatedVehicle);
                });
    }

    public Optional<VehicleImportDto> recordDutyPayment(Long vehicleId, BigDecimal paidAmount) {
        return vehicleImportRepository.findById(vehicleId)
                .map(vehicle -> {
                    vehicle.setDutyPaid(paidAmount);
                    
                    // If full duty is paid, update status
                    if (vehicle.getDutyCalculated() != null && 
                        paidAmount.compareTo(vehicle.getDutyCalculated()) >= 0) {
                        vehicle.setStatus(VehicleStatus.APPROVED);
                        vehicle.setApprovalDate(LocalDateTime.now());
                    }
                    
                    VehicleImport updatedVehicle = vehicleImportRepository.save(vehicle);
                    return convertToDto(updatedVehicle);
                });
    }

    public Optional<VehicleImportDto> assignRegistrationNumber(Long vehicleId, String registrationNumber) {
        return vehicleImportRepository.findById(vehicleId)
                .map(vehicle -> {
                    vehicle.setRegistrationNumber(registrationNumber);
                    VehicleImport updatedVehicle = vehicleImportRepository.save(vehicle);
                    return convertToDto(updatedVehicle);
                });
    }

    public boolean deleteVehicle(Long id) {
        if (vehicleImportRepository.existsById(id)) {
            vehicleImportRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private VehicleImportDto convertToDto(VehicleImport vehicle) {
        VehicleImportDto dto = new VehicleImportDto();
        dto.setId(vehicle.getId());
        dto.setVehicleId(vehicle.getVehicleId());
        dto.setMake(vehicle.getMake());
        dto.setModel(vehicle.getModel());
        dto.setYear(vehicle.getYear());
        dto.setEngineNumber(vehicle.getEngineNumber());
        dto.setChassisNumber(vehicle.getChassisNumber());
        dto.setOrigin(vehicle.getOrigin());
        dto.setDeclaredValue(vehicle.getDeclaredValue());
        dto.setEngineCapacity(vehicle.getEngineCapacity());
        dto.setFuelType(vehicle.getFuelType());
        dto.setColor(vehicle.getColor());
        dto.setStatus(vehicle.getStatus());
        dto.setSubmissionDate(vehicle.getSubmissionDate());
        dto.setInspectionDate(vehicle.getInspectionDate());
        dto.setApprovalDate(vehicle.getApprovalDate());
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setUpdatedAt(vehicle.getUpdatedAt());
        dto.setRemarks(vehicle.getRemarks());
        dto.setDutyCalculated(vehicle.getDutyCalculated());
        dto.setDutyPaid(vehicle.getDutyPaid());
        dto.setRegistrationNumber(vehicle.getRegistrationNumber());
        
        if (vehicle.getInspector() != null) {
            dto.setInspectorName(vehicle.getInspector().getFirstName() + " " + vehicle.getInspector().getLastName());
        }
        
        return dto;
    }
}
