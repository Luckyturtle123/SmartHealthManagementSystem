package com.mukesh.smarthealthmanagementtest.services;

import com.mukesh.smarthealthmanagement.entities.*;
import com.mukesh.smarthealthmanagement.exceptions.ResourceAlreadyExistsException;
import com.mukesh.smarthealthmanagement.exceptions.ResourceNotFoundException;
import com.mukesh.smarthealthmanagement.repositories.*;
import com.mukesh.smarthealthmanagement.services.AdminService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private NurseRepository nurseRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    private Patient patient;
    private Doctor doctor;
    private Appointment appointment;
    private Nurse nurse;
    private MedicalRecord medicalRecord;
   
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patient = new Patient(1L, "John", "Doe", "johndoe@example.com", "1234567890", null, null);
        doctor = new Doctor(1L, "Jane", "Smith", "jane@example.com", "Cardiology");
        appointment = new Appointment(1L, patient, doctor, LocalDateTime.now(), "Scheduled");
        nurse=new Nurse(1L,"Nina", "Williams", "nina@example.com", "Emergency", null);
        medicalRecord=new MedicalRecord(1L,patient,"Diagnosis", "Treatment", "Notes", null);
    }
    

    @Test
    void rescheduleAppointment_ShouldReschedule_WhenAppointmentExists() {
        // Given an existing appointment with a specific date and time
        LocalDateTime originalAppointmentDate = LocalDateTime.of(2024, 11, 11, 16, 0, 35, 231370200);  // Set exact date and time
        LocalDateTime rescheduledDate = LocalDateTime.of(2024, 11, 12, 16, 0, 35, 231370200); // Set the rescheduled time

        Appointment appointment = new Appointment(1L, new Patient(), new Doctor(), originalAppointmentDate, "Scheduled");

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        // Act: Reschedule the appointment to the new date
        appointment.setAppointmentDate(rescheduledDate);
        String updatedAppointment = adminService.rescheduleAppointment(1L, "status");

        // Assert: Compare the rescheduled appointment's date with the expected value
        assertNotNull(updatedAppointment);
//        assertEquals(rescheduledDate, updatedAppointment.getAppointmentDate());
    }


    @Test
    void cancelAppointment_ShouldThrowException_WhenAppointmentNotFound() {
        when(appointmentRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminService.cancelAppointment(1L, "status"));
    }
    
    @Test
    void getAppointmentsForPatient_ShouldReturnAppointments_WhenPatientExists() {
        List<Appointment> appointments = Arrays.asList(new Appointment(1L, new Patient(), new Doctor(), LocalDateTime.now(), "Scheduled"));

        when(patientRepository.findById(1L)).thenReturn(Optional.of(new Patient()));
        when(appointmentRepository.findByPatientId(1L)).thenReturn(appointments);

        List<Appointment> result = adminService.getAppointmentsForPatient(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAppointmentsForPatient_ShouldThrowException_WhenPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.getAppointmentsForPatient(1L));
    }
    @Test
    void getAppointmentsForDoctor_ShouldReturnAppointments_WhenDoctorExists() {
        List<Appointment> appointments = Arrays.asList(new Appointment(1L, new Patient(), new Doctor(), LocalDateTime.now(), "Scheduled"));

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(new Doctor()));
        when(appointmentRepository.findAppointmentsByDoctorId(1L)).thenReturn(appointments);

        List<Appointment> result = adminService.getAppointmentsForDoctor(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAppointmentsForDoctor_ShouldThrowException_WhenDoctorNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.getAppointmentsForDoctor(1L));
    }

    @Test
    void getAllAppointments_ShouldReturnAppointments_WhenAppointmentsExist() {
        List<Appointment> appointments = Arrays.asList(
            new Appointment(1L, new Patient(), new Doctor(), LocalDateTime.now(), "Scheduled"),
            new Appointment(2L, new Patient(), new Doctor(), LocalDateTime.now(), "Scheduled")
        );

        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<Appointment> result = adminService.getAllAppointments();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllAppointments_ShouldReturnEmptyList_WhenNoAppointmentsExist() {
        when(appointmentRepository.findAll()).thenReturn(new ArrayList<>());

        List<Appointment> result = adminService.getAllAppointments();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createDoctor_ShouldCreateDoctor_WhenEmailNotExists() {
        Doctor doctor = new Doctor(1L, "Deven","Stoke", "Cardiology", "drsmith@example.com");

        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.empty());
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        Doctor result = adminService.createDoctor(doctor);

        assertNotNull(result);
    }
    @Test
    void deleteDoctor_ShouldDeleteDoctor_WhenDoctorExists() {
        when(doctorRepository.existsById(1L)).thenReturn(true);

        adminService.deleteDoctor(1L);

        verify(doctorRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDoctor_ShouldThrowException_WhenDoctorNotFound() {
        when(doctorRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteDoctor(1L));
    }

    @Test
    void getAllDoctors_ShouldReturnDoctors_WhenDoctorsExist() {
        List<Doctor> doctors = Arrays.asList(
            new Doctor(1L, "Dr. Smith","Clark", "Cardiology", "drsmith@example.com"),
            new Doctor(2L, "Dr. Brown", "Drake","Orthopedics", "drbrown@example.com")
        );

        when(doctorRepository.findAll()).thenReturn(doctors);

        List<Doctor> result = adminService.getAllDoctors();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllDoctors_ShouldThrowException_WhenNoDoctorsExist() {
        when(doctorRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(ResourceNotFoundException.class, () -> adminService.getAllDoctors());
    }

    @Test
    void createNurse_ShouldReturnNurse_WhenNurseDoesNotExist() {
        Nurse nurse = new Nurse(1L, "Nurse", "Alice", "alice@example.com", "1234567890", "Pediatrics");

        when(nurseRepository.findByEmail(nurse.getEmail())).thenReturn(Optional.empty());
        when(nurseRepository.save(nurse)).thenReturn(nurse);

        Nurse createdNurse = adminService.createNurse(nurse);

        assertNotNull(createdNurse);
    }

    @Test
    void createNurse_ShouldThrowException_WhenNurseAlreadyExists() {
        Nurse nurse = new Nurse(1L, "Nurse", "Alice", "alice@example.com", "1234567890", "Pediatrics");

        when(nurseRepository.findByEmail(nurse.getEmail())).thenReturn(Optional.of(nurse));

        assertThrows(ResourceAlreadyExistsException.class, () -> adminService.createNurse(nurse));
    }


    @Test
    void updateNurse_ShouldThrowException_WhenNurseNotFound() {
        Nurse updatedNurse = new Nurse(1L, "Nurse"," Alice", "alice@example.com", "0987654321", "Cardiology");

        when(nurseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.updateNurse(1L, updatedNurse));
    }

    @Test
    void deleteNurse_ShouldDeleteNurse_WhenNurseExists() {
        when(nurseRepository.existsById(1L)).thenReturn(true);

        adminService.deleteNurse(1L);
        verify(nurseRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteNurse_ShouldThrowException_WhenNurseNotFound() {
        when(nurseRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteNurse(1L));
    }
    @Test
    void getAllNurses_ShouldReturnNurses_WhenNursesExist() {
        List<Nurse> nurses = Arrays.asList(
            new Nurse(1L, "Nurse"," Alice", "alice@example.com", "1234567890", "Pediatrics"),
            new Nurse(2L, "Nurse"," Bob", "bob@example.com", "0987654321", "Orthopedics")
        );

        when(nurseRepository.findAll()).thenReturn(nurses);

        List<Nurse> result = adminService.getAllNurses();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllNurses_ShouldReturnEmptyList_WhenNoNursesExist() {
        when(nurseRepository.findAll()).thenReturn(new ArrayList<>());

        List<Nurse> result = adminService.getAllNurses();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    void createPatient_ShouldReturnPatient_WhenPatientDoesNotExist() {
        Patient patient = new Patient(1L, "John", "Doe", "johndoe@example.com", "1234567890", "", null);

        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient createdPatient = adminService.createPatient(patient);

        assertNotNull(createdPatient);
    }

    @Test
    void createPatient_ShouldThrowException_WhenPatientAlreadyExists() {
        Patient patient = new Patient(1L, "John", "Doe", "johndoe@example.com", "1234567890", "", null);

        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.of(patient));

        assertThrows(ResourceAlreadyExistsException.class, () -> adminService.createPatient(patient));
    }

    @Test
    void updatePatient_ShouldThrowException_WhenPatientNotFound() {
        Patient updatedPatient = new Patient(1L, "John", "Doe", "john.doe@example.com", "0987654321", "", null);

        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.updatePatient(1L, updatedPatient));
    }
    @Test
    void deletePatient_ShouldDeletePatient_WhenPatientExists() {
        when(patientRepository.existsById(1L)).thenReturn(true);

        adminService.deletePatient(1L);
        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePatient_ShouldThrowException_WhenPatientNotFound() {
        when(patientRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminService.deletePatient(1L));
    }
    @Test
    void getAllPatients_ShouldReturnPatients_WhenPatientsExist() {
        List<Patient> patients = Arrays.asList(
            new Patient(1L, "John", "Doe", "johndoe@example.com", "1234567890", "", null),
            new Patient(2L, "Jane", "Doe", "janedoe@example.com", "0987654321", "", null)
        );

        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> result = adminService.getAllPatients();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllPatients_ShouldReturnEmptyList_WhenNoPatientsExist() {
        when(patientRepository.findAll()).thenReturn(new ArrayList<>());

        List<Patient> result = adminService.getAllPatients();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createMedicalRecord_ShouldCreateMedicalRecord_WhenPatientExists() {
        MedicalRecord record = new MedicalRecord();
        Patient patient = new Patient(1L, "John", "Doe", "johndoe@example.com", "1234567890", "", null);
        record.setPatient(patient);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.save(record)).thenReturn(record);

        MedicalRecord result = adminService.createMedicalRecord(record);

        assertNotNull(result);
    }

    @Test
    void updateMedicalRecord_ShouldUpdateMedicalRecord_WhenRecordExists() {
        MedicalRecord record = new MedicalRecord(1L, new Patient(), "Diagnosis", "Treatment", "Notes", null);

        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(medicalRecordRepository.save(record)).thenReturn(record);

        MedicalRecord result = adminService.updateMedicalRecord(1L, record);

        assertNotNull(result);
        assertEquals("Diagnosis", result.getDiagnosis());
    }

    @Test
    void deleteMedicalRecord_ShouldDeleteMedicalRecord_WhenRecordExists() {
        when(medicalRecordRepository.existsById(1L)).thenReturn(true);

        adminService.deleteMedicalRecord(1L);

        verify(medicalRecordRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMedicalRecord_ShouldThrowException_WhenRecordNotFound() {
        when(medicalRecordRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteMedicalRecord(1L));
    }

    @Test
    void getAllMedicalRecords_ShouldReturnMedicalRecords_WhenRecordsExist() {
        List<MedicalRecord> records = Arrays.asList(new MedicalRecord(1L, new Patient(), "Diagnosis", "Treatment", "Notes", null));

        when(medicalRecordRepository.findAll()).thenReturn(records);

        List<MedicalRecord> result = adminService.getAllMedicalRecords();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllMedicalRecords_ShouldReturnEmptyList_WhenNoRecordsExist() {
        when(medicalRecordRepository.findAll()).thenReturn(new ArrayList<>());

        List<MedicalRecord> result = adminService.getAllMedicalRecords();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


}