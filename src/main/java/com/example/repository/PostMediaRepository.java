package com.example.repository;

import com.example.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostMediaRepository
        extends JpaRepository<PostMedia, Long> {

    List<PostMedia> findByPostId(Long postId);
}

