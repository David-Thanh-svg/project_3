package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;   // 🔥 DB user.id (BIGINT)

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean shared = false;

    @Column(name = "original_post_id")
    private Long originalPostId;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}



