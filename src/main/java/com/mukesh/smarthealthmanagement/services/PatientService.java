package com.mukesh.smarthealthmanagement.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import com.mukesh.smarthealthmanagement.entities.MedicalRecord;
import com.mukesh.smarthealthmanagement.entities.Patient;
import com.mukesh.smarthealthmanagement.exceptions.ResourceNotFoundException;
import com.mukesh.smarthealthmanagement.repositories.AppointmentRepository;
import com.mukesh.smarthealthmanagement.repositories.MedicalRecordRepository;
import com.mukesh.smarthealthmanagement.repositories.PatientRepository;

import jakarta.xml.bind.ValidationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    

    public Patient createPatient(Patient patient) throws ValidationException {
        validatePatientData(patient); // Validate patient data before creation
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Patient with ID " + id + " not found"));
    }

    public Patient updatePatient(Long id, Patient updatedPatient) throws ValidationException {
        validatePatientData(updatedPatient); // Validate patient data before updating

        Patient patient = getPatientById(id); // Throws exception if not found

        patient.setFirstName(updatedPatient.getFirstName());
        patient.setLastName(updatedPatient.getLastName());
        patient.setEmail(updatedPatient.getEmail());
        patient.setPhoneNumber(updatedPatient.getPhoneNumber());
        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new IllegalArgumentException("Patient with ID " + id + " does not exist.");
        }
        patientRepository.deleteById(id);
    }

    @Transactional
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord.getPatient() == null) {
            throw new IllegalArgumentException("Patient cannot be null.");
        }

        // Validate if patient exists
        Optional<Patient> patientOpt = patientRepository.findById(medicalRecord.getPatient().getId());
        if (!patientOpt.isPresent()) {
            throw new RuntimeException("Patient not found.");
        }

        // Save the medical record if all validations pass
        return medicalRecordRepository.save(medicalRecord);
    }

    @Transactional
    public MedicalRecord updateMedicalRecord(Long id, MedicalRecord updatedMedicalRecord) {
        return medicalRecordRepository.findById(id).map(medicalRecord -> {
            medicalRecord.setDiagnosis(updatedMedicalRecord.getDiagnosis());
            medicalRecord.setTreatment(updatedMedicalRecord.getTreatment());
            medicalRecord.setNotes(updatedMedicalRecord.getNotes());
            medicalRecord.setPatient(updatedMedicalRecord.getPatient());
            return medicalRecordRepository.save(medicalRecord);
        }).orElseThrow(() -> new ResourceNotFoundException("Medical Record not found"));
    }

    public List<String> getAppointmentsForPatient(Long id) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(id);
        return appointments.stream()
            .map(appointment -> {
                String doctorName = appointment.getDoctor() != null ? appointment.getDoctor().getFirstName() : "Unknown Doctor";
                return "Appointment with Dr. " + doctorName
                        + " on " + appointment.getAppointmentDate()
                        + " - Status: " + appointment.getStatus();
            })
            .collect(Collectors.toList());
    }

    // Validation for Patient data
    private void validatePatientData(Patient patient) throws ValidationException {
        if (patient.getFirstName() == null || patient.getFirstName().isEmpty()) {
            throw new ValidationException("Patient first name cannot be null or empty");
        }
        if (patient.getLastName() == null || patient.getLastName().isEmpty()) {
            throw new ValidationException("Patient last name cannot be null or empty");
        }
        if (patient.getEmail() == null || !patient.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid patient email format");
        }
        if (patient.getPhoneNumber() == null || !patient.getPhoneNumber().matches("\\d{10}")) {
            throw new ValidationException("Patient phone number must be a 10-digit number");
        }
    }
}