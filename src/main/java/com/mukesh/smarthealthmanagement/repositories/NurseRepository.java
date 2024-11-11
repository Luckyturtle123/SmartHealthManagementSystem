package com.mukesh.smarthealthmanagement.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mukesh.smarthealthmanagement.entities.Nurse;

public interface NurseRepository extends JpaRepository<Nurse, Long> {
	//Finding nurse by email to avoid multiple entries of same data
	Optional<Nurse> findByEmail(String email);
}

