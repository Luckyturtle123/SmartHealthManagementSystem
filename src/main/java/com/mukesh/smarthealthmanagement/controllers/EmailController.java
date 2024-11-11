package com.mukesh.smarthealthmanagement.controllers;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import com.mukesh.smarthealthmanagement.services.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    //Endpoint to send a notification
    @PostMapping("/sendNotification")
    public String sendAppointmentNotification(@RequestParam String to, @RequestBody Appointment appointment) {
        emailService.sendAppointmentNotification(to, appointment);
        return "Email sent successfully to " + to;
    }
}
