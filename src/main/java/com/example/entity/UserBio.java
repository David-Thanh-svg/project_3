package com.example.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_bio")
@Getter
@Setter
public class UserBio {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JsonBackReference // <-- quan trọng: bỏ qua khi serialize Userprofile
    @JoinColumn(name = "user_id")
    private Userprofile user;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "website")
    private String website;

    @Column(name = "location")
    private String location;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
