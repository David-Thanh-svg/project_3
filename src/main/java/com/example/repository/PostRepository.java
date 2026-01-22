package com.example.repository;

import com.example.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long userId);

    @Query("""
        select p from Post p
        where
            p.privacy = 'PUBLIC'
            or (p.privacy = 'FRIENDS'
                and exists (
                    select 1 from User u
                    join u.followers f
                    where u = p.author and f.id = :viewerId
                ))
            or p.author.id = :viewerId
        order by p.createdAt desc
    """)
    List<Post> findFeed(@Param("viewerId") Long viewerId);

}

