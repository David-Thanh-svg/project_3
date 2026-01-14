package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "post_media")
@Getter
@Setter
public class PostMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 🔥 DÒNG QUAN TRỌNG NHẤT
    @Column(name = "media_url", nullable = false)
    private String url;

    @Column(nullable = false)
    private String type;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}

