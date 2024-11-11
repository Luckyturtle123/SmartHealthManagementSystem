package com.mukesh.smarthealthmanagement.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mukesh.smarthealthmanagement.entities.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	//Method is used to find the doctors by mail id
	Optional<Doctor> findByEmail(String email);
}
