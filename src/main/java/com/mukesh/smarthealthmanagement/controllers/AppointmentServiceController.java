package com.mukesh.smarthealthmanagement.controllers;

import com.mukesh.smarthealthmanagement.entities.Appointment;
import com.mukesh.smarthealthmanagement.services.AppointmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentServiceController {
    @Autowired
    private AppointmentService appointmentService;

    //End point to book and Appointment
    @PostMapping
    public ResponseEntity<Appointment> bookAppointment(@RequestParam Long patientId, @RequestParam Long doctorId, @RequestParam LocalDateTime appointmentDate) {
        Appointment appointment = appointmentService.bookAppointment(patientId, doctorId, appointmentDate);
        return ResponseEntity.ok(appointment);
    }

    //Endpoint to reschedule any appointment
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> rescheduleAppointment(@PathVariable Long id, @RequestParam LocalDateTime newDate) {
        Appointment appointment = appointmentService.rescheduleAppointment(id, newDate);
        return ResponseEntity.ok(appointment);
    }

    //Endpoint to delete any appoint
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    //Endpoint to get appointments based on patientId
    @GetMapping("/patients/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsForPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsForPatient(patientId));
    }

    //Endpoint to get appointments based on doctorId
    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<Optional<Appointment>> getAppointmentsForDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsForDoctor(doctorId));
    }
}
