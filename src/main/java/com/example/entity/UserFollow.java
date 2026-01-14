package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_follow")
@Getter
@Setter
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người đang follow
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private Userprofile follower;

    // Người được follow
    @ManyToOne
    @JoinColumn(name = "following_id")
    private Userprofile following;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
