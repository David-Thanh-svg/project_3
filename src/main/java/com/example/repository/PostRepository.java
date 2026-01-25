package com.example.repository;

import com.example.entity.Post;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long userId);

    @Query("""
         select distinct p from Post p
         left join fetch p.media
         left join fetch p.taggedUsers
         join fetch p.author a
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


            @Query("""
            select p from Post p
            left join fetch p.media
            where p.id = :id
        """)
    Optional<Post> findByIdWithMedia(Long id);




}

