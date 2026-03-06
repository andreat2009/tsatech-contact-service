package com.newproject.contact.repository;

import com.newproject.contact.domain.ContactMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findAllByOrderByCreatedAtDesc();
    List<ContactMessage> findByStatusOrderByCreatedAtDesc(String status);
}
