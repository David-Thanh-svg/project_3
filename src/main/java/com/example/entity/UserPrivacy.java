package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_privacy")
@Getter
@Setter
public class UserPrivacy {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Userprofile user;

    @Column(name = "show_followers")
    private Boolean showFollowers;

    @Column(name = "show_following")
    private Boolean showFollowing;

    @Column(name = "show_posts")
    private Boolean showPosts;

    @Column(name = "allow_messages")
    private Boolean allowMessages;

    @Column(name = "show_phone")
    private Boolean showPhone;
}

