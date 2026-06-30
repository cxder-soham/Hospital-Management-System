package com.flexi.soham.hospitalManagement.repository;

import com.flexi.soham.hospitalManagement.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
}