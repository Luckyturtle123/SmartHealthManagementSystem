package com.mukesh.smarthealthmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mukesh.smarthealthmanagement.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	//Find the user by username
    Optional<User> findByUsername(String username);
    //Finding the user by both either username or email
    User findByUsernameOrEmail(String username, String email);
}
