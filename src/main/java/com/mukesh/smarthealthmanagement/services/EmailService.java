package com.mukesh.smarthealthmanagement.services;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendAppointmentNotification(String to, Appointment appointment) {
        // Validating email address format (basic check)
        if (!isValidEmail(to)) {
            throw new IllegalArgumentException("Invalid email address: " + to);
        }

        // Creating email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Appointment Scheduled");
        
        // Constructing the message with detailed appointment information
        String appointmentDetails = "Dear Patient,\n\n" +
                                    "Your appointment with Dr. " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName() +
                                    " has been scheduled for " + appointment.getAppointmentDate() + ".\n\n" +
                                    "Please ensure you arrive 15 minutes prior to your appointment time.\n\n" +
                                    "Thank you for choosing our healthcare service.";
        
        message.setText(appointmentDetails);

        try {
            emailSender.send(message);
        } catch (Exception e) {
            // Handle failure in sending email (e.g., log the error or rethrow with a custom message)
            throw new RuntimeException("Failed to send appointment notification email to " + to, e);
        }
    }

    // Simple method to check if an email address is valid 
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}