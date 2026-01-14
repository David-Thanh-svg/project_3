package com.example.repository;

import com.example.entity.Userprofile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserprofileRepository
        extends JpaRepository<Userprofile, Long> {

    Optional<Userprofile> findByKeycloakId(String keycloakId);
}

