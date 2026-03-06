package com.newproject.contact.controller;

import com.newproject.contact.dto.ContactMessageRequest;
import com.newproject.contact.dto.ContactMessageResponse;
import com.newproject.contact.service.ContactMessageService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cms/contact")
public class ContactMessageController {
    private final ContactMessageService service;

    public ContactMessageController(ContactMessageService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactMessageResponse create(@Valid @RequestBody ContactMessageRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ContactMessageResponse> list(@RequestParam(required = false) String status) {
        return service.list(status);
    }

    @PatchMapping("/{id}/status")
    public ContactMessageResponse updateStatus(@PathVariable Long id, @RequestParam String status) {
        return service.updateStatus(id, status);
    }
}
