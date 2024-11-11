package com.mukesh.smarthealthmanagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import com.mukesh.smarthealthmanagement.entities.Doctor;
import com.mukesh.smarthealthmanagement.entities.MedicalRecord;
import com.mukesh.smarthealthmanagement.entities.Patient;
import com.mukesh.smarthealthmanagement.services.AppointmentService;
import com.mukesh.smarthealthmanagement.services.DoctorService;
import com.mukesh.smarthealthmanagement.services.MedicalRecordService;
import com.mukesh.smarthealthmanagement.services.PatientService;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/api/auth/login/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private MedicalRecordService medicalRecordService;

    @GetMapping("/getalldoctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id);
        return doctor != null ? ResponseEntity.ok(doctor) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{doctorId}/patients/{patientId}")
    public ResponseEntity<Patient> updatePatientRecord(@PathVariable Long doctorId, @PathVariable Long patientId, @RequestBody Patient updatedPatient) {
        return ResponseEntity.ok(doctorService.updatePatientRecord(doctorId, patientId, updatedPatient));
    }
    
    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        return appointmentService.getAppointmentsByDoctorId(doctorId);
    }
    
    @PostMapping("/addmedicalreport")
    public ResponseEntity<String> addMedicalReport(
            @RequestParam MultipartFile file, 
            @RequestParam String diagnosis, 
            @RequestParam String treatment, 
            @RequestParam String notes,
            @RequestParam Long patientId) throws IOException {

        // Ensuring patientId is being received correctly
        if (patientId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Patient ID is required");
        }

        // Fetching the patient from the database
        Patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Patient not found");
        }

        // Creating and populating the MedicalRecord object
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setDiagnosis(diagnosis);
        medicalRecord.setTreatment(treatment);
        medicalRecord.setNotes(notes);
        medicalRecord.setPatient(patient);  

        // Saving the MedicalRecord
        medicalRecordService.createMedicalRecord(medicalRecord, file);

        return ResponseEntity.ok("Medical report uploaded successfully");
    }

}
