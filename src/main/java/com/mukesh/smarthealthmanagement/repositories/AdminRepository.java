package com.mukesh.smarthealthmanagement.repositories;

import com.mukesh.smarthealthmanagement.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
  
}
