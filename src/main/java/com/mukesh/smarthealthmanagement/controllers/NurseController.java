package com.mukesh.smarthealthmanagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mukesh.smarthealthmanagement.entities.Nurse;
import com.mukesh.smarthealthmanagement.services.NurseService;

import java.util.List;

@RestController
@RequestMapping("/api/auth/nurses")
public class NurseController {
    @Autowired
    private NurseService nurseService;
    
    //End point to get list of all nurses
    @GetMapping("/getAllNurses")
    public ResponseEntity<List<Nurse>> getAllNurses() {
        return ResponseEntity.ok(nurseService.getAllNurses());
    }

    //End point to get the details of nurse by giving id
    @GetMapping("/getby/{id}")
    public ResponseEntity<Nurse> getNurseById(@PathVariable Long id) {
        Nurse nurse = nurseService.getNurseById(id);
        return nurse != null ? ResponseEntity.ok(nurse) : ResponseEntity.notFound().build();
    }
}
