package com.mukesh.smarthealthmanagementtest.services;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import com.mukesh.smarthealthmanagement.entities.Patient;
import com.mukesh.smarthealthmanagement.repositories.PatientRepository;
import com.mukesh.smarthealthmanagement.repositories.AppointmentRepository;
import com.mukesh.smarthealthmanagement.services.PatientService;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private PatientService patientService;

    private Patient validPatient;

    @BeforeEach
    public void setup() {
        validPatient = new Patient();
        validPatient.setId(1L);
        validPatient.setFirstName("John");
        validPatient.setLastName("Doe");
        validPatient.setEmail("john.doe@example.com");
        validPatient.setPhoneNumber("1234567890");
    }

    @Test
    public void testCreatePatient_Success() throws ValidationException {
        when(patientRepository.save(any(Patient.class))).thenReturn(validPatient);
        
        Patient createdPatient = patientService.createPatient(validPatient);
        
        assertNotNull(createdPatient);
        assertEquals(validPatient.getFirstName(), createdPatient.getFirstName());
    }

    @Test
    public void testCreatePatient_ValidationException() {
        validPatient.setEmail("invalid-email");
        
        assertThrows(ValidationException.class, () -> patientService.createPatient(validPatient));
    }

    @Test
    public void testGetAllPatients() {
        when(patientRepository.findAll()).thenReturn(Arrays.asList(validPatient));

        List<Patient> patients = patientService.getAllPatients();

        assertNotNull(patients);
        assertFalse(patients.isEmpty());
    }

    @Test
    public void testGetPatientById_Success() {
        when(patientRepository.findById(1L)).thenReturn(java.util.Optional.of(validPatient));

        Patient patient = patientService.getPatientById(1L);

        assertNotNull(patient);
        assertEquals(validPatient.getFirstName(), patient.getFirstName());
    }

    @Test
    public void testGetPatientById_NotFound() {
        when(patientRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> patientService.getPatientById(1L));
    }

    @Test
    public void testUpdatePatient_Success() throws ValidationException {
        when(patientRepository.findById(1L)).thenReturn(java.util.Optional.of(validPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(validPatient);

        validPatient.setPhoneNumber("0987654321");
        Patient updatedPatient = patientService.updatePatient(1L, validPatient);

        assertNotNull(updatedPatient);
        assertEquals(validPatient.getPhoneNumber(), updatedPatient.getPhoneNumber());
    }

    @Test
    public void testUpdatePatient_ValidationException() {
        validPatient.setPhoneNumber("invalid");
        
        assertThrows(ValidationException.class, () -> patientService.updatePatient(1L, validPatient));
    }

    @Test
    public void testDeletePatient_Success() {
        when(patientRepository.existsById(1L)).thenReturn(true);

        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeletePatient_NotFound() {
        when(patientRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> patientService.deletePatient(1L));
    }

    @Test
    public void testGetAppointmentsForPatient_Success() {
        Appointment appointment = new Appointment();
        LocalDateTime now = LocalDateTime.now();
        appointment.setDoctor(null);  // No doctor assigned
        appointment.setAppointmentDate(now);
        appointment.setStatus("Scheduled");

        when(appointmentRepository.findByPatientId(1L)).thenReturn(Arrays.asList(appointment));

        List<String> appointments = patientService.getAppointmentsForPatient(1L);

        assertNotNull(appointments);
        assertFalse(appointments.isEmpty());
        assertTrue(appointments.get(0).contains("Appointment with Dr. Unknown Doctor"));
    }

    @Test
    public void testValidatePatientData_InvalidEmail() {
        validPatient.setEmail("invalid-email");
        
        assertThrows(ValidationException.class, () -> patientService.createPatient(validPatient));
    }

    @Test
    public void testValidatePatientData_InvalidPhoneNumber() {
        validPatient.setPhoneNumber("12345");
        
        assertThrows(ValidationException.class, () -> patientService.createPatient(validPatient));
    }
    
    @Test
    public void testValidatePatientData() {
    	Patient patient = new Patient();
    	patient.setFirstName("");
    	patient.setLastName("");
        
    	assertThrows(ValidationException.class, () -> patientService.createPatient(patient));
    }
    
    @Test
    public void testValidatePatientData1() {
    	Patient patient = new Patient();
    	patient.setFirstName("firstName");
    	patient.setLastName("");
        
    	assertThrows(ValidationException.class, () -> patientService.createPatient(patient));
    }
}
