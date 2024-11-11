package com.mukesh.smarthealthmanagement.controllers;

import com.mukesh.smarthealthmanagement.entities.Feedback;
import com.mukesh.smarthealthmanagement.services.FeedbackService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    //End point to post the feedback based on the doctor Id
    @PostMapping("/doctor/{doctorId}")
    public Feedback submitFeedback(@PathVariable Long doctorId, @RequestBody Feedback feedback) {
        return feedbackService.addFeedback(doctorId, feedback);
    }
    
    //End point to get the feedback based on the doctor Id
    @GetMapping("/doctor/{doctorId}")
    public List<Feedback> getFeedbackByDoctorId(@PathVariable Long doctorId) {
        return feedbackService.getFeedbackByDoctorId(doctorId);
    }

}
