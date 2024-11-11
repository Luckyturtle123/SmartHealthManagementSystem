package com.mukesh.smarthealthmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer doctorBehaviour;
    private Integer doctorDiagnosis;
    private Integer doctorTimeSense;
    private Integer doctorRecovery;
    private Integer doctorCommunication;
    private Integer overallRating;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @JsonBackReference
    private Doctor doctor; 


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDoctorBehaviour() {
        return doctorBehaviour;
    }

    public void setDoctorBehaviour(Integer doctorBehaviour) {
        this.doctorBehaviour = doctorBehaviour;
    }

    public Integer getDoctorDiagnosis() {
        return doctorDiagnosis;
    }

    public void setDoctorDiagnosis(Integer doctorDiagnosis) {
        this.doctorDiagnosis = doctorDiagnosis;
    }

    public Integer getDoctorTimeSense() {
        return doctorTimeSense;
    }

    public void setDoctorTimeSense(Integer doctorTimeSense) {
        this.doctorTimeSense = doctorTimeSense;
    }

    public Integer getDoctorRecovery() {
        return doctorRecovery;
    }

    public void setDoctorRecovery(Integer doctorRecovery) {
        this.doctorRecovery = doctorRecovery;
    }

    public Integer getDoctorCommunication() {
        return doctorCommunication;
    }

    public void setDoctorCommunication(Integer doctorCommunication) {
        this.doctorCommunication = doctorCommunication;
    }

    public Integer getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}
