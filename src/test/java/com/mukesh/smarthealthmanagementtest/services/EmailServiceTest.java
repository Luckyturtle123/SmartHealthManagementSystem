package com.mukesh.smarthealthmanagementtest.services;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import com.mukesh.smarthealthmanagement.entities.Doctor;
import com.mukesh.smarthealthmanagement.services.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendAppointmentNotification_Success() {
    	 // Arrange
        String patientEmail = "patient@example.com";
        Appointment appointment = new Appointment();
        Doctor doctor = new Doctor();
        doctor.setFirstName("john");
        doctor.setLastName("doe");
        appointment.setDoctor(doctor); // Mock the doctor
        appointment.setAppointmentDate(LocalDateTime.of(2024, 12, 15, 10, 30)); // Mock the appointment date
        
        // Construct expected email content with formatted date
        String expectedText = "Dear Patient,\n\n" +
                              "Your appointment with Dr. John Doe has been scheduled for 2024-12-15.\n\n" +
                              "Please ensure you arrive 15 minutes prior to your appointment time.\n\n" +
                              "Thank you for choosing our healthcare service.";
        
        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setTo(patientEmail);
        expectedMessage.setSubject("Appointment Scheduled");
        expectedMessage.setText(expectedText);

        // Act
        emailService.sendAppointmentNotification(patientEmail, appointment);

        assertNotNull(expectedMessage);
    }

    @Test
    void testSendAppointmentNotification_InvalidEmail() {
        // Arrange
        String invalidEmail = "invalid-email";
        Appointment appointment = createTestAppointment();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                emailService.sendAppointmentNotification(invalidEmail, appointment));

        assertEquals("Invalid email address: " + invalidEmail, exception.getMessage());
        verify(emailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendAppointmentNotification_EmailSenderException() {
        // Arrange
        String email = "patient@example.com";
        Appointment appointment = createTestAppointment();
        doThrow(new RuntimeException("Email sending failure")).when(emailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendAppointmentNotification(email, appointment));

        assertTrue(exception.getMessage().contains("Failed to send appointment notification email to " + email));
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // Helper method to create a sample Appointment with Doctor information
    private Appointment createTestAppointment() {
    	LocalDateTime now = LocalDateTime.now();
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(now);

        return appointment;
    }
}
