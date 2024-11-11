package com.mukesh.smarthealthmanagement.services;

import com.mukesh.smarthealthmanagement.entities.Doctor;
import com.mukesh.smarthealthmanagement.entities.Feedback;
import com.mukesh.smarthealthmanagement.repositories.DoctorRepository;
import com.mukesh.smarthealthmanagement.repositories.FeedbackRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public Feedback submitFeedback(Long doctorId, Feedback feedback) {
        return addFeedback(doctorId, feedback);
    }

    public Feedback addFeedback(Long doctorId, Feedback feedback) {
        try {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new NoSuchElementException("Doctor not found"));

            validateFeedbackRatings(feedback); // Validate each rating value before proceeding

            feedback.setDoctor(doctor); // Set the doctor object on feedback
            feedback.setOverallRating(calculateOverallRating(feedback)); // Setting the overall rating

            // Save feedback and update doctor details
            feedbackRepository.save(feedback);
            doctor.getFeedbacks().add(feedback);
            doctor.updateAverageRating();
            doctorRepository.save(doctor);

            return feedback;

        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Doctor with ID " + doctorId + " not found", e);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Data integrity violation while saving feedback", e);
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation exception with a descriptive message
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while submitting feedback", e);
        }
    }

    public List<Feedback> getFeedbackByDoctorId(Long doctorId) {
        try {
            return feedbackRepository.findByDoctorId(doctorId);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while retrieving feedback for doctor ID " + doctorId, e);
        }
    }

    private Integer calculateOverallRating(Feedback feedback) {
        int sum = feedback.getDoctorBehaviour() +
                  feedback.getDoctorDiagnosis() +
                  feedback.getDoctorTimeSense() +
                  feedback.getDoctorRecovery() +
                  feedback.getDoctorCommunication();
        return sum / 5;
    }

    private void validateFeedbackRatings(Feedback feedback) {
        // List of feedback values to check
        int[] ratings = {
            feedback.getDoctorBehaviour(),
            feedback.getDoctorDiagnosis(),
            feedback.getDoctorTimeSense(),
            feedback.getDoctorRecovery(),
            feedback.getDoctorCommunication()
        };

        // Validating each rating
        for (int rating : ratings) {
            if (rating < 0 || rating > 5) {
                throw new IllegalArgumentException("Each rating value must be between 0 and 5");
            }
        }
    }
}