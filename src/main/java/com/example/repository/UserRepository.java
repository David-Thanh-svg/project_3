package com.example.repository;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("""
        select case when count(u) > 0 then true else false end
        from User u
        join u.followers f
        where u.id = :ownerId and f.id = :viewerId
    """)
    boolean isFollower(@Param("ownerId") Long ownerId,
                       @Param("viewerId") Long viewerId);

    List<User> findByUsernameContainingIgnoreCase(String q);


}


