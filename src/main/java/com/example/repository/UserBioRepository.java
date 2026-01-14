package com.example.repository;

import com.example.entity.UserBio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserBioRepository extends JpaRepository<UserBio, Long> {
    Optional<UserBio> findByUserId(Long userId);
}
