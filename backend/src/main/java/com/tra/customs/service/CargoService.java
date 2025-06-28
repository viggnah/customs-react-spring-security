package com.tra.customs.service;

import com.tra.customs.dto.CargoEntryDto;
import com.tra.customs.dto.CreateCargoEntryRequest;
import com.tra.customs.dto.UpdateCargoEntryRequest;
import com.tra.customs.entity.CargoEntry;
import com.tra.customs.entity.CargoStatus;
import com.tra.customs.entity.User;
import com.tra.customs.repository.CargoEntryRepository;
import com.tra.customs.repository.UserRepository;
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
public class CargoService {

    @Autowired
    private CargoEntryRepository cargoEntryRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<CargoEntryDto> getAllCargo(Pageable pageable) {
        return cargoEntryRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    public Page<CargoEntryDto> getCargoByStatus(CargoStatus status, Pageable pageable) {
        return cargoEntryRepository.findByStatus(status, pageable)
                .map(this::convertToDto);
    }

    public Optional<CargoEntryDto> getCargoById(Long id) {
        return cargoEntryRepository.findById(id)
                .map(this::convertToDto);
    }

    public Optional<CargoEntryDto> getCargoByCargoId(String cargoId) {
        return cargoEntryRepository.findByCargoId(cargoId)
                .map(this::convertToDto);
    }

    public CargoEntryDto createCargo(CreateCargoEntryRequest request) {
        // Check if cargo ID already exists
        if (cargoEntryRepository.findByCargoId(request.getCargoId()).isPresent()) {
            throw new IllegalArgumentException("Cargo ID already exists: " + request.getCargoId());
        }

        CargoEntry cargo = new CargoEntry();
        cargo.setCargoId(request.getCargoId());
        cargo.setDescription(request.getDescription());
        cargo.setOrigin(request.getOrigin());
        cargo.setDestination(request.getDestination());
        cargo.setDeclaredValue(request.getDeclaredValue());
        cargo.setWeight(request.getWeight());
        cargo.setVolume(request.getVolume());
        cargo.setHsCode(request.getHsCode());
        cargo.setArrivalDate(request.getArrivalDate());
        cargo.setRemarks(request.getRemarks());
        cargo.setStatus(CargoStatus.PENDING_INSPECTION);

        CargoEntry savedCargo = cargoEntryRepository.save(cargo);
        return convertToDto(savedCargo);
    }

    public Optional<CargoEntryDto> updateCargo(Long id, UpdateCargoEntryRequest request) {
        return cargoEntryRepository.findById(id)
                .map(cargo -> {
                    if (request.getDescription() != null) {
                        cargo.setDescription(request.getDescription());
                    }
                    if (request.getOrigin() != null) {
                        cargo.setOrigin(request.getOrigin());
                    }
                    if (request.getDestination() != null) {
                        cargo.setDestination(request.getDestination());
                    }
                    if (request.getDeclaredValue() != null) {
                        cargo.setDeclaredValue(request.getDeclaredValue());
                    }
                    if (request.getWeight() != null) {
                        cargo.setWeight(request.getWeight());
                    }
                    if (request.getVolume() != null) {
                        cargo.setVolume(request.getVolume());
                    }
                    if (request.getHsCode() != null) {
                        cargo.setHsCode(request.getHsCode());
                    }
                    if (request.getStatus() != null) {
                        cargo.setStatus(request.getStatus());
                    }
                    if (request.getArrivalDate() != null) {
                        cargo.setArrivalDate(request.getArrivalDate());
                    }
                    if (request.getInspectionDate() != null) {
                        cargo.setInspectionDate(request.getInspectionDate());
                    }
                    if (request.getClearanceDate() != null) {
                        cargo.setClearanceDate(request.getClearanceDate());
                    }
                    if (request.getRemarks() != null) {
                        cargo.setRemarks(request.getRemarks());
                    }
                    if (request.getDutyCalculated() != null) {
                        cargo.setDutyCalculated(request.getDutyCalculated());
                    }
                    if (request.getDutyPaid() != null) {
                        cargo.setDutyPaid(request.getDutyPaid());
                    }

                    CargoEntry updatedCargo = cargoEntryRepository.save(cargo);
                    return convertToDto(updatedCargo);
                });
    }

    public Optional<CargoEntryDto> assignInspector(Long cargoId, Long inspectorId) {
        Optional<CargoEntry> cargoOpt = cargoEntryRepository.findById(cargoId);
        Optional<User> inspectorOpt = userRepository.findById(inspectorId);

        if (cargoOpt.isPresent() && inspectorOpt.isPresent()) {
            CargoEntry cargo = cargoOpt.get();
            User inspector = inspectorOpt.get();
            
            cargo.setInspector(inspector);
            cargo.setStatus(CargoStatus.UNDER_INSPECTION);
            cargo.setInspectionDate(LocalDateTime.now());
            
            CargoEntry updatedCargo = cargoEntryRepository.save(cargo);
            return Optional.of(convertToDto(updatedCargo));
        }
        
        return Optional.empty();
    }

    public Optional<CargoEntryDto> calculateDuty(Long cargoId, BigDecimal dutyAmount) {
        return cargoEntryRepository.findById(cargoId)
                .map(cargo -> {
                    cargo.setDutyCalculated(dutyAmount);
                    CargoEntry updatedCargo = cargoEntryRepository.save(cargo);
                    return convertToDto(updatedCargo);
                });
    }

    public Optional<CargoEntryDto> recordDutyPayment(Long cargoId, BigDecimal paidAmount) {
        return cargoEntryRepository.findById(cargoId)
                .map(cargo -> {
                    cargo.setDutyPaid(paidAmount);
                    
                    // If full duty is paid, update status
                    if (cargo.getDutyCalculated() != null && 
                        paidAmount.compareTo(cargo.getDutyCalculated()) >= 0) {
                        cargo.setStatus(CargoStatus.CLEARED);
                        cargo.setClearanceDate(LocalDateTime.now());
                    }
                    
                    CargoEntry updatedCargo = cargoEntryRepository.save(cargo);
                    return convertToDto(updatedCargo);
                });
    }

    public boolean deleteCargo(Long id) {
        if (cargoEntryRepository.existsById(id)) {
            cargoEntryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private CargoEntryDto convertToDto(CargoEntry cargo) {
        CargoEntryDto dto = new CargoEntryDto();
        dto.setId(cargo.getId());
        dto.setCargoId(cargo.getCargoId());
        dto.setDescription(cargo.getDescription());
        dto.setOrigin(cargo.getOrigin());
        dto.setDestination(cargo.getDestination());
        dto.setDeclaredValue(cargo.getDeclaredValue());
        dto.setWeight(cargo.getWeight());
        dto.setVolume(cargo.getVolume());
        dto.setHsCode(cargo.getHsCode());
        dto.setStatus(cargo.getStatus());
        dto.setArrivalDate(cargo.getArrivalDate());
        dto.setInspectionDate(cargo.getInspectionDate());
        dto.setClearanceDate(cargo.getClearanceDate());
        dto.setCreatedAt(cargo.getCreatedAt());
        dto.setUpdatedAt(cargo.getUpdatedAt());
        dto.setRemarks(cargo.getRemarks());
        dto.setDutyCalculated(cargo.getDutyCalculated());
        dto.setDutyPaid(cargo.getDutyPaid());
        
        if (cargo.getInspector() != null) {
            dto.setInspectorName(cargo.getInspector().getFirstName() + " " + cargo.getInspector().getLastName());
        }
        
        return dto;
    }
}
