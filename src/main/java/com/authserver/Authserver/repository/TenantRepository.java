package com.authserver.Authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authserver.Authserver.model.Tenant;

import java.util.Optional;


@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {

    Optional<Tenant> findByTenantId(String tenantId);
}