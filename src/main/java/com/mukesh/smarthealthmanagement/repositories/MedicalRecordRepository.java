package com.mukesh.smarthealthmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mukesh.smarthealthmanagement.entities.MedicalRecord;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
	//To delete a patient, we should also delete the medical records of that patient
	@Modifying
	@Query("DELETE FROM MedicalRecord mr WHERE mr.patient.id = :patient_id")
	void deleteByPatientId(@Param("patient_id") Long patientId);
}
