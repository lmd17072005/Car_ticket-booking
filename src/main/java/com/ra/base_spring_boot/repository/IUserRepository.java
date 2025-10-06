package com.ra.base_spring_boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ra.base_spring_boot.model.user.User;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByEmail(String email);
}
