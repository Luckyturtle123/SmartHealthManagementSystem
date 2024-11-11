package com.mukesh.smarthealthmanagementtest.services;

import com.mukesh.smarthealthmanagement.entities.MedicalRecord;
import com.mukesh.smarthealthmanagement.entities.Patient;
import com.mukesh.smarthealthmanagement.repositories.MedicalRecordRepository;
import com.mukesh.smarthealthmanagement.repositories.PatientRepository;
import com.mukesh.smarthealthmanagement.services.MedicalRecordService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
//import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private MedicalRecord medicalRecord;
    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        
        medicalRecord = new MedicalRecord();
        medicalRecord.setId(1L);
        medicalRecord.setDiagnosis("Diagnosis");
        medicalRecord.setTreatment("Treatment");
        medicalRecord.setNotes("Notes");
        medicalRecord.setPatient(patient);
    }

    @Test
    void createMedicalRecord_ShouldSaveRecord_WhenValidData() throws IOException {
        // Arrange
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(file.isEmpty()).thenReturn(false);
        when(file.getBytes()).thenReturn("test file data".getBytes());
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);

        // Act
        MedicalRecord createdRecord = medicalRecordService.createMedicalRecord(medicalRecord, file);

        // Assert
        assertNotNull(createdRecord);
        assertEquals(medicalRecord.getDiagnosis(), createdRecord.getDiagnosis());
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    @Test
    void createMedicalRecord_ShouldThrowException_WhenFileIsEmpty() {
        // Arrange
        when(file.isEmpty()).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                medicalRecordService.createMedicalRecord(medicalRecord, file));
        assertEquals("File cannot be empty", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    @Test
    void createMedicalRecord_ShouldThrowException_WhenPatientNotFound() {
        // Arrange
        when(file.isEmpty()).thenReturn(false);
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                medicalRecordService.createMedicalRecord(medicalRecord, file));
        assertEquals("Patient not found", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    @Test
    void getAllMedicalRecords_ShouldReturnListOfRecords() {
        // Arrange
        when(medicalRecordRepository.findAll()).thenReturn(List.of(medicalRecord));

        // Act
        List<MedicalRecord> records = medicalRecordService.getAllMedicalRecords();

        // Assert
        assertEquals(1, records.size());
        verify(medicalRecordRepository).findAll();
    }

    @Test
    void getMedicalRecordById_ShouldReturnRecord_WhenRecordExists() {
        // Arrange
        when(medicalRecordRepository.findById(medicalRecord.getId())).thenReturn(Optional.of(medicalRecord));

        // Act
        MedicalRecord foundRecord = medicalRecordService.getMedicalRecordById(medicalRecord.getId());

        // Assert
        assertNotNull(foundRecord);
        assertEquals(medicalRecord.getId(), foundRecord.getId());
        verify(medicalRecordRepository).findById(medicalRecord.getId());
    }

    @Test
    void getMedicalRecordById_ShouldReturnNull_WhenRecordDoesNotExist() {
        // Arrange
        when(medicalRecordRepository.findById(medicalRecord.getId())).thenReturn(Optional.empty());

        // Act
        MedicalRecord foundRecord = medicalRecordService.getMedicalRecordById(medicalRecord.getId());

        // Assert
        assertNull(foundRecord);
        verify(medicalRecordRepository).findById(medicalRecord.getId());
    }

    @Test
    void updateMedicalRecord_ShouldUpdateRecord_WhenRecordExists() {
        // Arrange
        MedicalRecord updatedRecord = new MedicalRecord();
        updatedRecord.setDiagnosis("Updated Diagnosis");
        updatedRecord.setTreatment("Updated Treatment");
        updatedRecord.setNotes("Updated Notes");

        when(medicalRecordRepository.findById(medicalRecord.getId())).thenReturn(Optional.of(medicalRecord));
        when(medicalRecordRepository.save(medicalRecord)).thenReturn(medicalRecord);

        // Act
        MedicalRecord result = medicalRecordService.updateMedicalRecord(medicalRecord.getId(), updatedRecord);

        // Assert
        assertEquals("Updated Diagnosis", result.getDiagnosis());
        assertEquals("Updated Treatment", result.getTreatment());
        verify(medicalRecordRepository).save(medicalRecord);
    }

    @Test
    void updateMedicalRecord_ShouldReturnNull_WhenRecordDoesNotExist() {
        // Arrange
        MedicalRecord updatedRecord = new MedicalRecord();
        when(medicalRecordRepository.findById(medicalRecord.getId())).thenReturn(Optional.empty());

        // Act
        MedicalRecord result = medicalRecordService.updateMedicalRecord(medicalRecord.getId(), updatedRecord);

        // Assert
        assertNull(result);
        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    @Test
    void deleteMedicalRecord_ShouldDeleteRecord_WhenRecordExists() {
        // Arrange
        when(medicalRecordRepository.existsById(medicalRecord.getId())).thenReturn(true);

        // Act
        medicalRecordService.deleteMedicalRecord(medicalRecord.getId());

        // Assert
        verify(medicalRecordRepository).deleteById(medicalRecord.getId());
    }

    @Test
    void deleteMedicalRecord_ShouldThrowException_WhenRecordDoesNotExist() {
        // Arrange
        when(medicalRecordRepository.existsById(medicalRecord.getId())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                medicalRecordService.deleteMedicalRecord(medicalRecord.getId()));
        assertEquals("Medical Record not found", exception.getMessage());
        verify(medicalRecordRepository, never()).deleteById(medicalRecord.getId());
    }
}
