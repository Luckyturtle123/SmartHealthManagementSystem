package com.mukesh.smarthealthmanagementtest.services;

import com.mukesh.smarthealthmanagement.entities.Doctor;
import com.mukesh.smarthealthmanagement.entities.Feedback;
import com.mukesh.smarthealthmanagement.repositories.DoctorRepository;
import com.mukesh.smarthealthmanagement.repositories.FeedbackRepository;
import com.mukesh.smarthealthmanagement.services.FeedbackService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    @Test
    public void testSubmitFeedback_Success() {
        // Arrange
        Long doctorId = 1L;
        Feedback feedback = new Feedback();
        feedback.setDoctorBehaviour(4);
        feedback.setDoctorDiagnosis(5);
        feedback.setDoctorTimeSense(3);
        feedback.setDoctorRecovery(4);
        feedback.setDoctorCommunication(5);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setFeedbacks(new ArrayList<>());  // Initialize the feedback list

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(feedbackRepository.save(feedback)).thenReturn(feedback);

        // Act
        Feedback result = feedbackService.submitFeedback(doctorId, feedback);

        // Assert
        assertNotNull(result);
        assertEquals(feedback, result);
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    public void testAddFeedback_DoctorNotFound() {
        // Arrange
        Long doctorId = 1L;
        Feedback feedback = new Feedback();
        feedback.setDoctorBehaviour(4);
        feedback.setDoctorDiagnosis(5);
        feedback.setDoctorTimeSense(3);
        feedback.setDoctorRecovery(4);
        feedback.setDoctorCommunication(5);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.addFeedback(doctorId, feedback);
        });
        assertEquals("Doctor with ID 1 not found", exception.getMessage());
    }

    @Test
    public void testAddFeedback_InvalidRating() {
        // Arrange
        Long doctorId = 1L;
        Feedback feedback = new Feedback();
        feedback.setDoctorBehaviour(6); // Invalid rating, should be between 0 and 5
        feedback.setDoctorDiagnosis(5);
        feedback.setDoctorTimeSense(4);
        feedback.setDoctorRecovery(3);
        feedback.setDoctorCommunication(5);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.addFeedback(doctorId, feedback);
        });
        assertEquals("Each rating value must be between 0 and 5", exception.getMessage());
    }

    @Test
    public void testAddFeedback_DataIntegrityViolation() {
        // Arrange
        Long doctorId = 1L;
        Feedback feedback = new Feedback();
        feedback.setDoctorBehaviour(4);
        feedback.setDoctorDiagnosis(5);
        feedback.setDoctorTimeSense(3);
        feedback.setDoctorRecovery(4);
        feedback.setDoctorCommunication(5);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(feedbackRepository.save(feedback)).thenThrow(new DataIntegrityViolationException("Data integrity violation"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            feedbackService.addFeedback(doctorId, feedback);
        });
        assertEquals("Data integrity violation while saving feedback", exception.getMessage());
    }

    @Test
    public void testAddFeedback_UnexpectedError() {
        // Arrange
        Long doctorId = 1L;
        Feedback feedback = new Feedback();
        feedback.setDoctorBehaviour(4);
        feedback.setDoctorDiagnosis(5);
        feedback.setDoctorTimeSense(3);
        feedback.setDoctorRecovery(4);
        feedback.setDoctorCommunication(5);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(feedbackRepository.save(feedback)).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            feedbackService.addFeedback(doctorId, feedback);
        });
        assertEquals("Unexpected error while submitting feedback", exception.getMessage());
    }

    @Test
    public void testGetFeedbackByDoctorId_Success() {
        // Arrange
        Long doctorId = 1L;
        Feedback feedback = new Feedback();
        when(feedbackRepository.findByDoctorId(doctorId)).thenReturn(List.of(feedback));

        // Act
        List<Feedback> result = feedbackService.getFeedbackByDoctorId(doctorId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void testGetFeedbackByDoctorId_Error() {
        // Arrange
        Long doctorId = 1L;
        when(feedbackRepository.findByDoctorId(doctorId)).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            feedbackService.getFeedbackByDoctorId(doctorId);
        });
        assertEquals("Unexpected error while retrieving feedback for doctor ID 1", exception.getMessage());
    }
}
