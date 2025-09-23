package com.wafipix.wafipix.modules.contact.repository;

import com.wafipix.wafipix.modules.contact.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    @Query("SELECT c FROM Contact c LEFT JOIN FETCH c.replies WHERE c.id = :id")
    Optional<Contact> findByIdWithReplies(@Param("id") UUID id);

    @Query("SELECT c FROM Contact c ORDER BY c.createdAt DESC")
    Page<Contact> findAllOrdered(Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.status = :status ORDER BY c.createdAt DESC")
    Page<Contact> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.readBy IS NULL ORDER BY c.createdAt DESC")
    List<Contact> findUnreadContacts();

    @Query("SELECT COUNT(c) FROM Contact c WHERE c.readBy IS NULL")
    long countUnreadContacts();
}
