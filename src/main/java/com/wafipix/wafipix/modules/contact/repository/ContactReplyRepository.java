package com.wafipix.wafipix.modules.contact.repository;

import com.wafipix.wafipix.modules.contact.entity.ContactReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContactReplyRepository extends JpaRepository<ContactReply, UUID> {
}
