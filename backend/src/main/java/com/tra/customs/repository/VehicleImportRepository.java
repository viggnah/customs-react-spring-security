package com.tra.customs.repository;

import com.tra.customs.entity.VehicleImport;
import com.tra.customs.entity.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleImportRepository extends JpaRepository<VehicleImport, Long> {
    
    Optional<VehicleImport> findByVehicleId(String vehicleId);
    
    List<VehicleImport> findByStatus(VehicleStatus status);
    
    Page<VehicleImport> findByStatus(VehicleStatus status, Pageable pageable);
    
    List<VehicleImport> findByMakeAndModel(String make, String model);
    
    List<VehicleImport> findByOrigin(String origin);
    
    List<VehicleImport> findBySubmissionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT v FROM VehicleImport v WHERE v.inspector.id = :inspectorId")
    List<VehicleImport> findByInspectorId(@Param("inspectorId") Long inspectorId);
    
    @Query("SELECT v FROM VehicleImport v WHERE v.status = :status AND v.submissionDate >= :fromDate")
    List<VehicleImport> findByStatusAndSubmissionDateAfter(@Param("status") VehicleStatus status, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT COUNT(v) FROM VehicleImport v WHERE v.status = :status")
    Long countByStatus(@Param("status") VehicleStatus status);
    
    Optional<VehicleImport> findByChassisNumber(String chassisNumber);
    
    Optional<VehicleImport> findByEngineNumber(String engineNumber);
}
