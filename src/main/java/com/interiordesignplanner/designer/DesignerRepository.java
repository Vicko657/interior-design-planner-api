package com.interiordesignplanner.designer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignerRepository extends JpaRepository<Designer, Long> {

    Optional<Designer> findByUserId(Long userId);

}
