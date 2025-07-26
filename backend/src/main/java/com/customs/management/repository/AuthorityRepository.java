package com.customs.management.repository;

import com.customs.management.entity.Authority;
import com.customs.management.entity.AuthorityName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    
    Optional<Authority> findByName(AuthorityName name);
    
    boolean existsByName(AuthorityName name);
}
