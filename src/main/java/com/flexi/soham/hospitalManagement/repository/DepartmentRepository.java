package com.flexi.soham.hospitalManagement.repository;

import com.flexi.soham.hospitalManagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}