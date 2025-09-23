package com.wafipix.wafipix.modules.client.repository;

import com.wafipix.wafipix.modules.client.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    boolean existsByTitleIgnoreCase(String title);

    boolean existsByTitleIgnoreCaseAndIdNot(String title, UUID id);

    @Query("SELECT c FROM Client c WHERE c.active = true ORDER BY c.createdAt DESC")
    List<Client> findActiveClients();

    @Query("SELECT c FROM Client c WHERE c.active = :active ORDER BY c.createdAt DESC")
    Page<Client> findByActive(@Param("active") Boolean active, Pageable pageable);

    @Query("SELECT c FROM Client c ORDER BY c.createdAt DESC")
    Page<Client> findAllOrdered(Pageable pageable);
}
