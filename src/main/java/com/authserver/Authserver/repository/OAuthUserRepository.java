package com.authserver.Authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authserver.Authserver.model.OAuthUser;

@Repository
public interface OAuthUserRepository extends JpaRepository<OAuthUser, String> {
    // find by googleId or email if needed
}