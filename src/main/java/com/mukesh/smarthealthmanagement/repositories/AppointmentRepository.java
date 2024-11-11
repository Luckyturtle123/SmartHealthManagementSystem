package com.mukesh.smarthealthmanagement.repositories;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Method to find appointments by patient ID
    List<Appointment> findByPatientId(Long patientId);
    
    //Method to find the appointments using doctor and patient id
    List<Appointment> findByDoctorIdAndPatientId(Long doctorId, Long patientId);
    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.doctor d " +
            "JOIN FETCH a.patient p " +
            "WHERE d.id = :doctorId")
    //Method to find the appointments using doctorId
     List<Appointment> findAppointmentsByDoctorId(@Param("doctorId") Long doctorId);
    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.doctor d " +
            "JOIN FETCH a.patient p ")
    
    //Method to get all the appointments assigned to a doctor
     List<Appointment> getAllDoctorAppointments();
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId")
    Long countVisitsByPatientId(@Param("patientId") Long patientId);
    //Inorder to delete a doctor we need to delete the all the appointments assigned to the doctor too
    @Modifying
    @Query("DELETE FROM Appointment a WHERE a.doctor.id = :doctorId")
    void deleteByDoctorId(@Param("doctorId") Long doctorId);


   
}
