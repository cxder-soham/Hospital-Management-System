package com.flexi.soham.hospitalManagement.repository;

import com.flexi.soham.hospitalManagement.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}