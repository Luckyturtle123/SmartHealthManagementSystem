package com.mukesh.smarthealthmanagement.services;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import com.mukesh.smarthealthmanagement.entities.Doctor;
import com.mukesh.smarthealthmanagement.entities.Patient;
import com.mukesh.smarthealthmanagement.repositories.AppointmentRepository;
import com.mukesh.smarthealthmanagement.repositories.DoctorRepository;
import com.mukesh.smarthealthmanagement.repositories.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private EmailService emailService; // Email service for notifications

    // Book an appointment
    public Appointment bookAppointment(Long patientId, Long doctorId, LocalDateTime appointmentDate) {
        // Check if patient and doctor exist
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> 
            new RuntimeException("Patient with ID " + patientId + " not found"));
        
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> 
            new RuntimeException("Doctor with ID " + doctorId + " not found"));

        // Create and set up the appointment
        Appointment appointment = new Appointment(null, patient, doctor, appointmentDate, "Scheduled");
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStatus("Scheduled");

        // Save the appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Send email notification
        emailService.sendAppointmentNotification(patient.getEmail(), savedAppointment);

        return savedAppointment;
    }

    // Reschedule an appointment
    public Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newDate) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment with ID " + appointmentId + " not found"));

        // Validate the new appointment date (example: it should not be in the past)
        if (newDate.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("New appointment date cannot be in the past");
        }

        // Update the appointment date and save the updated appointment
        appointment.setAppointmentDate(newDate);
        return appointmentRepository.save(appointment);
    }

    // Cancel an appointment
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment with ID " + appointmentId + " not found"));
        
        appointment.setStatus("Cancelled");
        
        // Save the updated appointment
        appointmentRepository.save(appointment);
    }

    // Get appointments for a specific patient
    public List<Appointment> getAppointmentsForPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new RuntimeException("Patient with ID " + patientId + " not found");
        }
        return appointmentRepository.findByPatientId(patientId);
    }

    // Get appointments for a specific doctor
    public Optional<Appointment> getAppointmentsForDoctor(Long doctorId) {
        return appointmentRepository.findById(doctorId);
    }

    // Get all appointments for a specific doctor
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findAppointmentsByDoctorId(doctorId);
    }

    // Get all doctor appointments
    public List<Appointment> getAllDoctorAppointments() {
        return appointmentRepository.getAllDoctorAppointments();
    }
}
