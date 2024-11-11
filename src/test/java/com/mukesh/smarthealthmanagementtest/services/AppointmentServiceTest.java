package com.mukesh.smarthealthmanagementtest.services;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import com.mukesh.smarthealthmanagement.entities.Doctor;
import com.mukesh.smarthealthmanagement.entities.Patient;
import com.mukesh.smarthealthmanagement.repositories.AppointmentRepository;
import com.mukesh.smarthealthmanagement.repositories.DoctorRepository;
import com.mukesh.smarthealthmanagement.repositories.PatientRepository;
import com.mukesh.smarthealthmanagement.services.AppointmentService;
import com.mukesh.smarthealthmanagement.services.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setEmail("patient@example.com");

        doctor = new Doctor();
        doctor.setId(1L);

        appointment = new Appointment(1L, patient, doctor, LocalDateTime.now().plusDays(1), "Scheduled");
    }

    @Test
    void bookAppointment_ShouldBookAppointment_WhenValidDataProvided() {
        // Arrange
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        Appointment bookedAppointment = appointmentService.bookAppointment(patient.getId(), doctor.getId(), appointment.getAppointmentDate());

        // Assert
        assertNotNull(bookedAppointment);
        assertEquals("Scheduled", bookedAppointment.getStatus());
        verify(appointmentRepository).save(any(Appointment.class));
        verify(emailService).sendAppointmentNotification(patient.getEmail(), bookedAppointment);
    }

    @Test
    void bookAppointment_ShouldThrowException_WhenPatientNotFound() {
        // Arrange
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                appointmentService.bookAppointment(patient.getId(), doctor.getId(), appointment.getAppointmentDate()));
        assertEquals("Patient with ID " + patient.getId() + " not found", exception.getMessage());
    }

    @Test
    void rescheduleAppointment_ShouldReschedule_WhenNewDateIsValid() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.now().plusDays(2);
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        Appointment rescheduledAppointment = appointmentService.rescheduleAppointment(appointment.getId(), newDate);

        // Assert
        assertNotNull(rescheduledAppointment);
        assertEquals(newDate, rescheduledAppointment.getAppointmentDate());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void rescheduleAppointment_ShouldThrowException_WhenNewDateIsInPast() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.now().minusDays(1);
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                appointmentService.rescheduleAppointment(appointment.getId(), newDate));
        assertEquals("New appointment date cannot be in the past", exception.getMessage());
    }

    @Test
    void cancelAppointment_ShouldCancelAppointment_WhenAppointmentExists() {
        // Arrange
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        // Act
        appointmentService.cancelAppointment(appointment.getId());

        // Assert
        assertEquals("Cancelled", appointment.getStatus());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void cancelAppointment_ShouldThrowException_WhenAppointmentNotFound() {
        // Arrange
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                appointmentService.cancelAppointment(appointment.getId()));
        assertEquals("Appointment with ID " + appointment.getId() + " not found", exception.getMessage());
    }

    @Test
    void getAppointmentsForPatient_ShouldReturnAppointments_WhenPatientExists() {
        // Arrange
        when(patientRepository.existsById(patient.getId())).thenReturn(true);
        when(appointmentRepository.findByPatientId(patient.getId())).thenReturn(List.of(appointment));

        // Act
        List<Appointment> appointments = appointmentService.getAppointmentsForPatient(patient.getId());

        // Assert
        assertNotNull(appointments);
        assertFalse(appointments.isEmpty());
        verify(appointmentRepository).findByPatientId(patient.getId());
    }

    @Test
    void getAppointmentsForPatient_ShouldThrowException_WhenPatientNotFound() {
        // Arrange
        when(patientRepository.existsById(patient.getId())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                appointmentService.getAppointmentsForPatient(patient.getId()));
        assertEquals("Patient with ID " + patient.getId() + " not found", exception.getMessage());
    }

    @Test
    void getAppointmentsForDoctor_ShouldReturnAppointment_WhenDoctorExists() {
        // Arrange
        when(appointmentRepository.findById(doctor.getId())).thenReturn(Optional.of(appointment));

        // Act
        Optional<Appointment> result = appointmentService.getAppointmentsForDoctor(doctor.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(appointment, result.get());
    }

    @Test
    void getAppointmentsByDoctorId_ShouldReturnListOfAppointments_WhenDoctorHasAppointments() {
        // Arrange
        when(appointmentRepository.findAppointmentsByDoctorId(doctor.getId())).thenReturn(List.of(appointment));

        // Act
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctor.getId());

        // Assert
        assertNotNull(appointments);
        assertFalse(appointments.isEmpty());
        verify(appointmentRepository).findAppointmentsByDoctorId(doctor.getId());
    }

    @Test
    void getAllDoctorAppointments_ShouldReturnListOfAppointments() {
        // Arrange
        when(appointmentRepository.getAllDoctorAppointments()).thenReturn(List.of(appointment));

        // Act
        List<Appointment> appointments = appointmentService.getAllDoctorAppointments();

        // Assert
        assertNotNull(appointments);
        assertFalse(appointments.isEmpty());
        verify(appointmentRepository).getAllDoctorAppointments();
    }
}
