package com.mukesh.smarthealthmanagement.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mukesh.smarthealthmanagement.entities.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
	//Finding patient by email
	Optional<Patient> findByEmail(String email);
}
