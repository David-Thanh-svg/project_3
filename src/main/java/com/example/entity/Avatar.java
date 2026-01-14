package com.example.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "avatars")
@Getter
@Setter
public class Avatar extends BaseEntity {

    /* =======================
       USER RELATION
       ======================= */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference // Chống vòng lặp vô tận khi trả về JSON
    private Userprofile user;

    /* =======================
       KEYCLOAK (AUDIT)
       ======================= */
    // Đã xóa updateAt vì đã có ở BaseEntity

    @Column(name = "keycloak_id", nullable = false)
    private String keycloakId;

    /* =======================
       AVATAR INFO
       ======================= */

    @Column(name = "object_name", nullable = false)
    private String objectName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    /* =======================
       STATUS
       ======================= */

    @Column(name = "is_active")
    private Boolean isActive = true;
}