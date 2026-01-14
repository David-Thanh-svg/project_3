package com.example.repository;

import com.example.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    List<Avatar> findByUserId(Long userId);
}
