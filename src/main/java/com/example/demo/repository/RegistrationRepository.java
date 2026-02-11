package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Registration;

public interface RegistrationRepository extends JpaRepository<Registration, Long>{
    
}
