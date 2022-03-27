package com.soccer.management.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.soccer.management.model.User;

/**
 * @author enes.boyaci
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    List<User> findByRole(String role);

    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "delete from User where email=:email")
    void deleteByEmail(@Param("email") String email);

}