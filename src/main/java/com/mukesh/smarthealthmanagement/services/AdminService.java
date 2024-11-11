package com.mukesh.smarthealthmanagement.services;

import com.mukesh.smarthealthmanagement.entities.*;
import com.mukesh.smarthealthmanagement.exceptions.ResourceAlreadyExistsException;
import com.mukesh.smarthealthmanagement.exceptions.ResourceNotFoundException;
import com.mukesh.smarthealthmanagement.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private NurseRepository nurseRepository;
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    // Method for admin to book an appointment
    @Transactional
    public Appointment bookAppointment(Long patientId, Long doctorId, String status) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);

        if (patientOpt.isPresent() && doctorOpt.isPresent()) {
            Appointment appointment = new Appointment();
            appointment.setPatient(patientOpt.get());
            appointment.setDoctor(doctorOpt.get());
            appointment.setStatus(status);
            appointment.setAppointmentDate(LocalDateTime.now());
            //appointment.setStatus("Scheduled");
            return appointmentRepository.save(appointment);
        } else {
            throw new ResourceNotFoundException("Patient or Doctor not found");
        }
    }

    // Method for admin to reschedule an existing appointment
    @Transactional
    public String rescheduleAppointment(Long appointmentId, String status) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        String statusMessage = "";
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(status);
            appointment.setAppointmentDate(LocalDateTime.now());
            Appointment appointmentData = appointmentRepository.save(appointment);
            if(!ObjectUtils.isEmpty(appointmentData)) {
                 statusMessage = "Appointment "+appointment.getStatus()+" to "+appointment.getAppointmentDate()+" for "+appointment.getPatient().getFirstName()+" "+appointment.getPatient().getLastName()+".";
            }
			return statusMessage;
        } else {
            throw new ResourceNotFoundException("Appointment not found");
        }
    }

    // Method for admin to cancel an appointment
    @Transactional
    public Appointment cancelAppointment(Long appointmentId,String status) {
    	Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
        	Appointment appointment = appointmentOpt.get();
        	appointment.setStatus(status);
        	appointment.setAppointmentDate(LocalDateTime.now());
            return appointmentRepository.save(appointment);
        } else {
            throw new ResourceNotFoundException("Appointment not found");
        }
    }

    // Method for admin to get all appointments for a specific patient
    public List<Appointment> getAppointmentsForPatient(Long patientId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);

        if (patientOpt.isPresent()) {
            return appointmentRepository.findByPatientId(patientId);
        } else {
            throw new ResourceNotFoundException("Patient not found");
        }
    }

    // Method for admin to get all appointments for a specific doctor
    public List<Appointment> getAppointmentsForDoctor(Long doctorId) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);

        if (doctorOpt.isPresent()) {
            return appointmentRepository.findAppointmentsByDoctorId(doctorId);
        } else {
            throw new ResourceNotFoundException("Doctor not found");
        }
    }

    // Method for admin to get all appointments
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        if (appointments.isEmpty()) {
            // Optionally log or take action when no appointments are found
            System.out.println("No appointments found.");
        }
        return appointments;
    }

    // Doctor Management
    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        // Check if doctor already exists by email or username (assuming uniqueness)
        Optional<Doctor> existingDoctor = doctorRepository.findByEmail(doctor.getEmail());
        if (existingDoctor.isPresent()) {
            throw new ResourceAlreadyExistsException("Doctor with this email already exists.");
        }

        return doctorRepository.save(doctor);
    }


    @Transactional
    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {
        return doctorRepository.findById(id).map(doctor -> {
            doctor.setFirstName(updatedDoctor.getFirstName());
            doctor.setLastName(updatedDoctor.getLastName());
            doctor.setSpecialty(updatedDoctor.getSpecialty());
            doctor.setEmail(updatedDoctor.getEmail());
            return doctorRepository.save(doctor);
        }).orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (doctorRepository.existsById(id)) {
        	 appointmentRepository.deleteByDoctorId(id);
            doctorRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Doctor not found");
        }
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        if (doctors.isEmpty()) {
            throw new ResourceNotFoundException("No doctors found.");
        }
        return doctors;
    }


    // Nurse Management
    @Transactional
    public Nurse createNurse(Nurse nurse) {
        // Check if a nurse with the same email already exists
        if (nurseRepository.findByEmail(nurse.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("A nurse with this email already exists.");
        }

        // Save the new nurse to the database
        return nurseRepository.save(nurse);
    }

    @Transactional
    public Nurse updateNurse(Long id, Nurse updatedNurse) {
        return nurseRepository.findById(id).map(nurse -> {
            nurse.setFirstName(updatedNurse.getFirstName());
            nurse.setLastName(updatedNurse.getLastName());
            nurse.setEmail(updatedNurse.getEmail());
            nurse.setPhoneNumber(updatedNurse.getPhoneNumber());
            nurse.setDepartment(updatedNurse.getDepartment());
            return nurseRepository.save(nurse);
        }).orElseThrow(() -> new ResourceNotFoundException("Nurse not found"));
    }

    @Transactional
    public void deleteNurse(Long id) {
        if (nurseRepository.existsById(id)) {
            nurseRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Nurse not found");
        }
    }

    public List<Nurse> getAllNurses() {
        List<Nurse> nurses = nurseRepository.findAll();
        if (nurses.isEmpty()) {
            // Optionally log or return a message
            System.out.println("No nurses found.");
        }
        return nurses;
    }

    // Patient Management
    @Transactional
    public Patient createPatient(Patient patient) {
        // Check if a patient with the same email or ID already exists
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("A patient with this email already exists.");
        }

        // Save the new patient
        return patientRepository.save(patient);
    }

    @Transactional
    public Patient updatePatient(Long id, Patient updatedPatient) {
        return patientRepository.findById(id).map(patient -> {
            patient.setFirstName(updatedPatient.getFirstName());
            patient.setLastName(updatedPatient.getLastName());
            patient.setEmail(updatedPatient.getEmail());
            patient.setPhoneNumber(updatedPatient.getPhoneNumber());
            return patientRepository.save(patient);
        }).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    @Transactional
    public void deletePatient(Long id) {
        if (patientRepository.existsById(id)) {
        	medicalRecordRepository.deleteByPatientId(id);
            patientRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Patient not found");
        }
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        if (patients.isEmpty()) {
            // Optionally log or return a message indicating no patients were found
            System.out.println("No patients found.");
        }
        return patients;
    }


    // Medical Record Management
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

    @Transactional
    public void deleteMedicalRecord(Long id) {
        if (medicalRecordRepository.existsById(id)) {
            medicalRecordRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Medical Record not found");
        }
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        List<MedicalRecord> medicalRecords = medicalRecordRepository.findAll();
        if (medicalRecords.isEmpty()) {
            // Optionally log or return a message indicating no records were found
            System.out.println("No medical records found.");
        }
        return medicalRecords;
    }
    
    public String getPatientVisits(Long patientId) {
    	Long visitCount = appointmentRepository.countVisitsByPatientId(patientId);
    	if(visitCount>0) {
    	return "Patient Visited "+visitCount+" time(s)";
    	}else {
    		return "Patient is not found in Smart Health Management System.";
    	}
    }

}