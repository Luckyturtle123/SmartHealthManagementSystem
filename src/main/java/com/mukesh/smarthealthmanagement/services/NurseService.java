package com.mukesh.smarthealthmanagement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.mukesh.smarthealthmanagement.entities.Nurse;
import com.mukesh.smarthealthmanagement.repositories.NurseRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NurseService {
    @Autowired
    private NurseRepository nurseRepository;

    public Nurse createNurse(Nurse nurse) {
        try {
            return nurseRepository.save(nurse);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid nurse data provided", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while creating nurse", e);
        }
    }

    public List<Nurse> getAllNurses() {
        try {
            return nurseRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while retrieving nurses", e);
        }
    }

    public Nurse getNurseById(Long id) {
        try {
            return nurseRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Nurse not found with ID: " + id));
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while retrieving nurse by ID " + id, e);
        }
    }

    public Nurse updateNurse(Long id, Nurse updatedNurse) {
        try {
            Nurse existingNurse = nurseRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Nurse not found with ID: " + id));

            validateNurseData(updatedNurse); // Ensure data is valid before updating

            existingNurse.setFirstName(updatedNurse.getFirstName());
            existingNurse.setLastName(updatedNurse.getLastName());
            existingNurse.setEmail(updatedNurse.getEmail());
            existingNurse.setPhoneNumber(updatedNurse.getPhoneNumber());
            existingNurse.setDepartment(updatedNurse.getDepartment());

            return nurseRepository.save(existingNurse);

        } catch (NoSuchElementException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid nurse data provided", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating nurse with ID " + id, e);
        }
    }

    public void deleteNurse(Long id) {
        try {
            if (!nurseRepository.existsById(id)) {
                throw new NoSuchElementException("Nurse not found with ID: " + id);
            }
            nurseRepository.deleteById(id);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while deleting nurse with ID " + id, e);
        }
    }

    public void validateNurseData(Nurse nurse) {
        if (nurse.getFirstName() == null || nurse.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("Nurse's first name cannot be null or empty");
        }
        if (nurse.getLastName() == null || nurse.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Nurse's last name cannot be null or empty");
        }
        if (nurse.getEmail() == null || !nurse.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email address for nurse");
        }
        if (nurse.getPhoneNumber() == null || nurse.getPhoneNumber().length() < 10) {
            throw new IllegalArgumentException("Invalid phone number for nurse");
        }
  
    }
}