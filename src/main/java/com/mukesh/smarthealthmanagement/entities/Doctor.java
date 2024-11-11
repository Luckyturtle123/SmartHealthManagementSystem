package com.mukesh.smarthealthmanagement.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;

//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonManagedReference;

//import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
//import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "doctors")
public class Doctor {
    public Doctor(Long id, String firstName, String lastName, String specialty, String email) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.specialty = specialty;
		this.email = email;
	}
    public Doctor() {
    	
    }
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Feedback> feedbacks;

    private double averageRating;

    public void updateAverageRating() {
        double total = feedbacks.stream()
                                .mapToDouble(Feedback::getOverallRating)
                                .average()
                                .orElse(0.0);
        BigDecimal roundedTotal = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
        this.averageRating = roundedTotal.doubleValue();
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String specialty;
    private String email;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Feedback> getFeedbacks() {
		return feedbacks;
	}
	public void setFeedbacks(List<Feedback> feedbacks) {
		this.feedbacks = feedbacks;
	}
	public double getAverageRating() {
		return averageRating;
	}
	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

}
