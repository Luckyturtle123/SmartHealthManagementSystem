package com.mukesh.smarthealthmanagement.repositories;

import com.mukesh.smarthealthmanagement.entities.Feedback;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	//Method to find the doctors feedback by doctorId
	List<Feedback> findByDoctorId(Long doctorId);
}
