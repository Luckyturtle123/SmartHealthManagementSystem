package com.mukesh.smarthealthmanagement.controllers;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import com.mukesh.smarthealthmanagement.entities.Doctor;
import com.mukesh.smarthealthmanagement.entities.MedicalRecord;
import com.mukesh.smarthealthmanagement.entities.Nurse;
import com.mukesh.smarthealthmanagement.entities.Patient;
import com.mukesh.smarthealthmanagement.repositories.MedicalRecordRepository;
import com.mukesh.smarthealthmanagement.services.AdminService;
import com.mukesh.smarthealthmanagement.services.AppointmentService;
import com.mukesh.smarthealthmanagement.services.DoctorService;
import com.mukesh.smarthealthmanagement.services.MedicalRecordService;
import com.mukesh.smarthealthmanagement.services.PatientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private MedicalRecordService medicalRecordService;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    // Endpoint to book an appointment
    @PostMapping("/appointments/booking")
    public ResponseEntity<Appointment> bookAppointment(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam String status) {
        Appointment appointment = adminService.bookAppointment(patientId, doctorId, status);
        return ResponseEntity.ok(appointment);
    }

    // Endpoint to reschedule an appointment
    @PutMapping("/appointments/reschedulebyid")
    public ResponseEntity<String> rescheduleAppointment(
    		@RequestParam Long id,
    		@RequestParam String status) {
        String appointment = adminService.rescheduleAppointment(id, status);
        return ResponseEntity.ok(appointment);
    }

    // Endpoint to cancel an appointment
    @PutMapping("/appointments/cancelbyid")
    public ResponseEntity<Appointment> cancelAppointment(@RequestParam Long id, @RequestParam String status) {
    	Appointment appointment = adminService.cancelAppointment(id,status);
        return ResponseEntity.ok(appointment);
    }

    // Endpoint to get appointments for a specific patient
    @GetMapping("/appointments/patients/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsForPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(adminService.getAppointmentsForPatient(patientId));
    }

    // Doctor Management
    //End point to create a doctor by admin
    @PostMapping("/doctors/create")
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        return ResponseEntity.ok(adminService.createDoctor(doctor));
    }
  //End point to update a doctor by admin
    @PutMapping("/doctors/update/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        return ResponseEntity.ok(adminService.updateDoctor(id, doctor));
    }
    
  //End point to delete a doctor by admin
    @DeleteMapping("/doctors/delete/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        adminService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
  //End point to get all doctors by admin
    @GetMapping("/doctors/doctorsList")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(adminService.getAllDoctors());
    }

    // Nurse Management
    //End point to create a nurse by admin
    @PostMapping("/nurses/create")
    public ResponseEntity<Nurse> createNurse(@RequestBody Nurse nurse) {
        return ResponseEntity.ok(adminService.createNurse(nurse));
    }
    //End point to update a nurse by admin
    @PutMapping("/nurses/update/{id}")
    public ResponseEntity<Nurse> updateNurse(@PathVariable Long id, @RequestBody Nurse nurse) {
        return ResponseEntity.ok(adminService.updateNurse(id, nurse));
    }
    // Endpoint to delete a nurse by giving nurse_id   
    @DeleteMapping("/nurses/delete/{id}")
    public ResponseEntity<Void> deleteNurse(@PathVariable Long id) {
        adminService.deleteNurse(id);
        return ResponseEntity.noContent().build();
    }

    //Endpoint to get all nurse details 
    @GetMapping("/nurses/nursesList")
    public ResponseEntity<List<Nurse>> getAllNurses() {
        return ResponseEntity.ok(adminService.getAllNurses());
    }

    // Patient Management
    //End point to create patient
    @PostMapping("/patients/create")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        return ResponseEntity.ok(adminService.createPatient(patient));
    }

    //End point to update patient 
    @PutMapping("/patients/update/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        return ResponseEntity.ok(adminService.updatePatient(id, patient));
    }
    //End point to delete a patient by id
    @DeleteMapping("/patients/delete/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        adminService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    //End point to get all patient details
    @GetMapping("/patients/patientsList")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(adminService.getAllPatients());
    }

    //End point to update patient by using doctor and patient id
    @PutMapping("/{doctorId}/patients/{patientId}")
    public ResponseEntity<Patient> updatePatientRecord(@PathVariable Long doctorId, @PathVariable Long patientId, @RequestBody Patient updatedPatient) {
        return ResponseEntity.ok(doctorService.updatePatientRecord(doctorId, patientId, updatedPatient));
    }
    //End point to get appointments of a doctor by giving doctor id
    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        return appointmentService.getAppointmentsByDoctorId(doctorId);
    }
    
    //End point to add any medical records of the patient
    @PostMapping("/addmedicalreport")
    public ResponseEntity<String> addMedicalReport(
            @RequestParam MultipartFile file, 
            @RequestParam String diagnosis, 
            @RequestParam String treatment, 
            @RequestParam String notes,
            @RequestParam Long patientId) throws IOException {

        // Ensuring that patientId is being received correctly
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

    //Upadating the medical records using id
    @PutMapping("/medicalrecords/update/{id}")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@PathVariable Long id, @RequestBody MedicalRecord medicalRecord) {
        return ResponseEntity.ok(adminService.updateMedicalRecord(id, medicalRecord));
    }

    //Deleting the medical recors using id
    @DeleteMapping("/medicalrecords/delete/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        adminService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }

    //End point to get all the medical records list 
    @GetMapping("/medicalrecords/medicalRecordsList")
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        return ResponseEntity.ok(adminService.getAllMedicalRecords());
    }
    

    //End point to get all the appointments assigned to all doctors
    @GetMapping("appointments/doctorsAppointments")
    public ResponseEntity<List<Appointment>> getAllDoctorAppointments() {
        return ResponseEntity.ok(appointmentService.getAllDoctorAppointments());
    }
    
    //Endpoint to get the patient visits
    @GetMapping("appointments/getPatientVisits")
    public ResponseEntity<String> getPatientVisits(@RequestParam Long patientId){
    	return ResponseEntity.ok(adminService.getPatientVisits(patientId));
    }
}
