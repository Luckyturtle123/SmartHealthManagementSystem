package com.mukesh.smarthealthmanagement.entities;

import java.util.List;

//import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient {
    

	public Patient(Long id, String firstName, String lastName, String email, String phoneNumber,
			String medicalHistoryDocumentPath, List<Appointment> appointments) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.medicalHistoryDocumentPath = medicalHistoryDocumentPath;
	}
	public Patient()
	{}

    private String phoneNumber;
    private String medicalHistoryDocumentPath;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMedicalHistoryDocumentPath() {
		return medicalHistoryDocumentPath;
	}

	public void setMedicalHistoryDocumentPath(String medicalHistoryDocumentPath) {
		this.medicalHistoryDocumentPath = medicalHistoryDocumentPath;
	}

	@Override
	public String toString() {
		return "Patient [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", phoneNumber=" + phoneNumber + ", medicalHistoryDocumentPath=" + medicalHistoryDocumentPath
				+"]";
	}

  
}
