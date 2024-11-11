package com.mukesh.smarthealthmanagement.services;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import com.mukesh.smarthealthmanagement.entities.Doctor;
import com.mukesh.smarthealthmanagement.entities.Patient;
import com.mukesh.smarthealthmanagement.repositories.AppointmentRepository;
import com.mukesh.smarthealthmanagement.repositories.DoctorRepository;
import com.mukesh.smarthealthmanagement.repositories.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {
        Optional<Doctor> existingDoctor = doctorRepository.findById(id);
        if (existingDoctor.isPresent()) {
            Doctor doctor = existingDoctor.get();
            doctor.setFirstName(updatedDoctor.getFirstName());
            doctor.setLastName(updatedDoctor.getLastName());
            doctor.setSpecialty(updatedDoctor.getSpecialty());
            doctor.setEmail(updatedDoctor.getEmail());
            return doctorRepository.save(doctor);
        }
        return null;
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    // Get all appointments for a specific doctor
    public List<Appointment> getAppointmentsForDoctor(Long doctorId) {
        return appointmentRepository.findAppointmentsByDoctorId(doctorId);
    }

    public Patient updatePatientRecord(Long doctorId, Long patientId, Patient updatedPatient) {
        Optional<Doctor> existingDoctorOptional = doctorRepository.findById(doctorId);
        if (!existingDoctorOptional.isPresent()) {
            throw new RuntimeException("Doctor not found"); 
        }

        Optional<Patient> existingPatientOptional = patientRepository.findById(patientId);
        if (!existingPatientOptional.isPresent()) {
            throw new RuntimeException("Patient not found");
        }

        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndPatientId(doctorId, patientId);
        if (appointments.isEmpty()) {
            throw new RuntimeException("Doctor is not authorized to update this patient's record");
        }

        Patient existingPatient = existingPatientOptional.get();
        existingPatient.setFirstName(updatedPatient.getFirstName());
        existingPatient.setLastName(updatedPatient.getLastName());
        existingPatient.setEmail(updatedPatient.getEmail());
        existingPatient.setPhoneNumber(updatedPatient.getPhoneNumber());
        existingPatient.setMedicalHistoryDocumentPath(updatedPatient.getMedicalHistoryDocumentPath());

        return patientRepository.save(existingPatient);
    }
    
}