package com.mukesh.smarthealthmanagement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mukesh.smarthealthmanagement.entities.MedicalRecord;
import com.mukesh.smarthealthmanagement.repositories.MedicalRecordRepository;
import com.mukesh.smarthealthmanagement.entities.Patient;
import com.mukesh.smarthealthmanagement.repositories.PatientRepository;

import java.io.IOException;
import java.util.List;

@Service
public class MedicalRecordService {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    // Method to create a medical record with a file
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord, MultipartFile file) throws IOException {
        // Validate the file (ensure it's not null or empty)
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Ensure the patient exists before saving the record
        Patient patient = medicalRecord.getPatient();
        if (patient == null || patientRepository.findById(patient.getId()).isEmpty()) {
            throw new IllegalArgumentException("Patient not found");
        }

        // Set the file data in the medical record
        MedicalRecord record = new MedicalRecord();
        record.setDiagnosis(medicalRecord.getDiagnosis());
        record.setTreatment(medicalRecord.getTreatment());
        record.setNotes(medicalRecord.getNotes());
        record.setPatient(medicalRecord.getPatient());

        // Convert the file to a byte array and store it in the entity
        record.setData(file.getBytes());

        // Save the medical record to the database
        return medicalRecordRepository.save(record);
    }

    // Method to get all medical records
    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    // Method to get a medical record by its ID
    public MedicalRecord getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id).orElse(null);
    }

    // Method to update a medical record
    public MedicalRecord updateMedicalRecord(Long id, MedicalRecord updatedRecord) {
        // Fetch existing record
        MedicalRecord record = getMedicalRecordById(id);
        if (record != null) {
            record.setDiagnosis(updatedRecord.getDiagnosis());
            record.setTreatment(updatedRecord.getTreatment());
            record.setNotes(updatedRecord.getNotes());
            record.setPatient(updatedRecord.getPatient());
            return medicalRecordRepository.save(record);
        }
        return null;  // Return null if the record doesn't exist
    }

    // Method to delete a medical record by ID
    public void deleteMedicalRecord(Long id) {
        // Ensure the record exists before deletion
        if (medicalRecordRepository.existsById(id)) {
            medicalRecordRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Medical Record not found");
        }
    }
}