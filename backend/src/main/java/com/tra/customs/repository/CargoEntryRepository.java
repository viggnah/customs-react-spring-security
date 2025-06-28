package com.tra.customs.repository;

import com.tra.customs.entity.CargoEntry;
import com.tra.customs.entity.CargoStatus;
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
public interface CargoEntryRepository extends JpaRepository<CargoEntry, Long> {
    
    Optional<CargoEntry> findByCargoId(String cargoId);
    
    List<CargoEntry> findByStatus(CargoStatus status);
    
    Page<CargoEntry> findByStatus(CargoStatus status, Pageable pageable);
    
    List<CargoEntry> findByOrigin(String origin);
    
    List<CargoEntry> findByArrivalDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT c FROM CargoEntry c WHERE c.inspector.id = :inspectorId")
    List<CargoEntry> findByInspectorId(@Param("inspectorId") Long inspectorId);
    
    @Query("SELECT c FROM CargoEntry c WHERE c.status = :status AND c.arrivalDate >= :fromDate")
    List<CargoEntry> findByStatusAndArrivalDateAfter(@Param("status") CargoStatus status, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT COUNT(c) FROM CargoEntry c WHERE c.status = :status")
    Long countByStatus(@Param("status") CargoStatus status);
}
