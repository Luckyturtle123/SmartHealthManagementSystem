package com.mukesh.smarthealthmanagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.mukesh.smarthealthmanagement.entities.Feedback;
import com.mukesh.smarthealthmanagement.entities.MedicalRecord;
import com.mukesh.smarthealthmanagement.entities.Patient;
import com.mukesh.smarthealthmanagement.services.FeedbackService;
import com.mukesh.smarthealthmanagement.services.MedicalRecordService;
import com.mukesh.smarthealthmanagement.services.PatientService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/auth/login/Patients")
public class PatientController {
    @Autowired
    private PatientService patientService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private MedicalRecordService medicalRecordService;

    @GetMapping("/getAllPatients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<String>> getAppointmentsForPatient(@PathVariable Long id) {
        List<String> appointments = patientService.getAppointmentsForPatient(id);
        return appointments != null ? ResponseEntity.ok(appointments) : ResponseEntity.notFound().build();
    
    }
    @GetMapping("/feedback/doctor/{doctorId}")
    public List<Feedback> getDoctorFeedback(@PathVariable Long doctorId) {
        return feedbackService.getFeedbackByDoctorId(doctorId);
    }

    @PostMapping("/feedback/doctor/{doctorId}")
    public Feedback submitFeedback(@PathVariable Long doctorId, @RequestBody Feedback feedback) {
        return feedbackService.submitFeedback(doctorId, feedback);
    }
    

 // Endpoint to upload a medical report by patient 
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
        medicalRecord.setPatient(patient);  // Associate the patient

        // Saving the MedicalRecord
        medicalRecordService.createMedicalRecord(medicalRecord, file);

        return ResponseEntity.ok("Medical report uploaded successfully");
    }

}
