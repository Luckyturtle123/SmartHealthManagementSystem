package com.mukesh.smarthealthmanagementtest.services;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import com.mukesh.smarthealthmanagement.entities.Doctor;
import com.mukesh.smarthealthmanagement.entities.Patient;
import com.mukesh.smarthealthmanagement.repositories.AppointmentRepository;
import com.mukesh.smarthealthmanagement.repositories.DoctorRepository;
import com.mukesh.smarthealthmanagement.repositories.PatientRepository;
import com.mukesh.smarthealthmanagement.services.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDoctor() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        when(doctorRepository.save(doctor)).thenReturn(doctor);

        Doctor savedDoctor = doctorService.createDoctor(doctor);
        assertNotNull(savedDoctor);
        assertEquals("John", savedDoctor.getFirstName());
        assertEquals("Doe", savedDoctor.getLastName());
    }

    @Test
    void testGetAllDoctors() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        when(doctorRepository.findAll()).thenReturn(Collections.singletonList(doctor));

        List<Doctor> doctors = doctorService.getAllDoctors();
        assertFalse(doctors.isEmpty());
        assertEquals(1, doctors.size());
        assertEquals("John", doctors.get(0).getFirstName());
    }

    @Test
    void testGetDoctorById() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("John");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        Doctor foundDoctor = doctorService.getDoctorById(1L);
        assertNotNull(foundDoctor);
        assertEquals(1L, foundDoctor.getId());
        assertEquals("John", foundDoctor.getFirstName());
    }

    @Test
    void testUpdateDoctor() {
        Doctor existingDoctor = new Doctor();
        existingDoctor.setId(1L);
        existingDoctor.setFirstName("John");

        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setFirstName("Jane");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existingDoctor));
        when(doctorRepository.save(existingDoctor)).thenReturn(existingDoctor);

        Doctor savedDoctor = doctorService.updateDoctor(1L, updatedDoctor);
        assertNotNull(savedDoctor);
        assertEquals("Jane", savedDoctor.getFirstName());
        
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        Doctor savedDoctor1 = doctorService.updateDoctor(1L, updatedDoctor);
        assertNull(savedDoctor1);
    }

    @Test
    void testDeleteDoctor() {
        long doctorId = 1L;

        doctorService.deleteDoctor(doctorId);
        verify(doctorRepository, times(1)).deleteById(doctorId);
    }

    @Test
    void testGetAppointmentsForDoctor() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        when(appointmentRepository.findAppointmentsByDoctorId(1L)).thenReturn(Collections.singletonList(appointment));

        List<Appointment> appointments = doctorService.getAppointmentsForDoctor(1L);
        assertFalse(appointments.isEmpty());
        assertEquals(1, appointments.size());
        assertEquals(1L, appointments.get(0).getId());
    }

    @Test
    void testUpdatePatientRecord() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");

        Patient updatedPatient = new Patient();
        updatedPatient.setFirstName("Jane");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByDoctorIdAndPatientId(1L, 1L)).thenReturn(Collections.singletonList(new Appointment()));
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient savedPatient = doctorService.updatePatientRecord(1L, 1L, updatedPatient);
        assertNotNull(savedPatient);
        assertEquals("Jane", savedPatient.getFirstName());
    }
    
    @Test
    void testUpdatePatientRecord1() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");

        Patient updatedPatient = new Patient();
        updatedPatient.setFirstName("Jane");

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> doctorService.updatePatientRecord(1L, 1L, updatedPatient), "Doctor not found");
    }
    
    @Test
    void testUpdatePatientRecord2() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");

        Patient updatedPatient = new Patient();
        updatedPatient.setFirstName("Jane");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> doctorService.updatePatientRecord(1L, 1L, updatedPatient), "Doctor not found");
    }
    
    @Test
    void testUpdatePatientRecord3() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");

        Patient updatedPatient = new Patient();
        updatedPatient.setFirstName("Jane");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByDoctorIdAndPatientId(1L, 1L)).thenReturn(new ArrayList<>());

        assertThrows(RuntimeException.class, () -> doctorService.updatePatientRecord(1L, 1L, updatedPatient), "Doctor not found");
    }
}

