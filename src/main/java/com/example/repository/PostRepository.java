package com.example.repository;

import com.example.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // ✅ userId là BIGINT
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
}
