package com.flexi.soham.hospitalManagement.repository;

import com.flexi.soham.hospitalManagement.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}