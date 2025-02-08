package com.authserver.Authserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authserver.Authserver.model.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    // find by googleId, etc.
    Optional<UserRole> findByGoogleId(String googleId);
}