package com.newproject.contact.service;

import com.newproject.contact.domain.ContactMessage;
import com.newproject.contact.dto.ContactMessageRequest;
import com.newproject.contact.dto.ContactMessageResponse;
import com.newproject.contact.events.EventPublisher;
import com.newproject.contact.exception.NotFoundException;
import com.newproject.contact.repository.ContactMessageRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactMessageService {
    private final ContactMessageRepository repository;
    private final EventPublisher eventPublisher;

    public ContactMessageService(ContactMessageRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ContactMessageResponse create(ContactMessageRequest request) {
        ContactMessage message = new ContactMessage();
        message.setName(request.getName());
        message.setEmail(request.getEmail());
        message.setSubject(request.getSubject());
        message.setMessage(request.getMessage());
        message.setStatus("NEW");
        OffsetDateTime now = OffsetDateTime.now();
        message.setCreatedAt(now);
        message.setUpdatedAt(now);

        ContactMessage saved = repository.save(message);
        ContactMessageResponse response = toResponse(saved);
        eventPublisher.publish("CONTACT_MESSAGE_CREATED", "contact_message", saved.getId().toString(), response);
        return response;
    }

    @Transactional(readOnly = true)
    public List<ContactMessageResponse> list(String status) {
        List<ContactMessage> messages = (status == null || status.isBlank())
            ? repository.findAllByOrderByCreatedAtDesc()
            : repository.findByStatusOrderByCreatedAtDesc(status.trim().toUpperCase(Locale.ROOT));
        return messages.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ContactMessageResponse updateStatus(Long id, String status) {
        ContactMessage message = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Contact message not found"));
        message.setStatus(status.trim().toUpperCase(Locale.ROOT));
        message.setUpdatedAt(OffsetDateTime.now());

        ContactMessage saved = repository.save(message);
        ContactMessageResponse response = toResponse(saved);
        eventPublisher.publish("CONTACT_MESSAGE_STATUS_UPDATED", "contact_message", saved.getId().toString(), response);
        return response;
    }

    private ContactMessageResponse toResponse(ContactMessage message) {
        ContactMessageResponse response = new ContactMessageResponse();
        response.setId(message.getId());
        response.setName(message.getName());
        response.setEmail(message.getEmail());
        response.setSubject(message.getSubject());
        response.setMessage(message.getMessage());
        response.setStatus(message.getStatus());
        response.setCreatedAt(message.getCreatedAt());
        response.setUpdatedAt(message.getUpdatedAt());
        return response;
    }
}
