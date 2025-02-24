package com.authserver.Authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.authserver.Authserver.model.TenantTicket;

import java.util.Optional;


public interface TenantTicketRepository extends JpaRepository<TenantTicket, Long> {
    
    Optional<TenantTicket> findByTicketId(String ticketId);

    Optional<TenantTicket> findByEsFindingId(String esFindingId);
}