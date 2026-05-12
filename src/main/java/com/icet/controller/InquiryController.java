package com.icet.controller;

import com.icet.model.dto.InquiryCreateDTO;
import com.icet.model.dto.InquiryDTO;
import com.icet.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@CrossOrigin
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    /**
     * Create a new inquiry
     * POST /api/inquiries
     */
    @PostMapping
    public ResponseEntity<InquiryDTO> createInquiry(@RequestBody InquiryCreateDTO dto) {
        InquiryDTO inquiry = inquiryService.createInquiry(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiry);
    }

    /**
     * Get inquiry by ID
     * GET /api/inquiries/{inquiryId}
     */
    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryDTO> getInquiryById(@PathVariable Long inquiryId) {
        InquiryDTO inquiry = inquiryService.getInquiryById(inquiryId);
        return ResponseEntity.ok(inquiry);
    }

    /**
     * Get inquiries by customer
     * GET /api/inquiries/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InquiryDTO>> getByCustomer(@PathVariable Long customerId) {
        List<InquiryDTO> inquiries = inquiryService.getInquiriesByCustomer(customerId);
        return ResponseEntity.ok(inquiries);
    }

    /**
     * Get inquiries by property
     * GET /api/properties/{propertyId}/inquiries
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<InquiryDTO>> getByProperty(@PathVariable Long propertyId) {
        List<InquiryDTO> inquiries = inquiryService.getInquiriesByProperty(propertyId);
        return ResponseEntity.ok(inquiries);
    }

    /**
     * Get unanswered inquiries for property (for seller)
     * GET /api/properties/{propertyId}/inquiries/unanswered
     */
    @GetMapping("/property/{propertyId}/unanswered")
    public ResponseEntity<List<InquiryDTO>> getUnanswered(@PathVariable Long propertyId) {
        List<InquiryDTO> inquiries = inquiryService.getUnansweredInquiries(propertyId);
        return ResponseEntity.ok(inquiries);
    }

    /**
     * Mark inquiry as replied
     * PUT /api/inquiries/{inquiryId}/reply
     */
    @PutMapping("/{inquiryId}/reply")
    public ResponseEntity<InquiryDTO> replyToInquiry(@PathVariable Long inquiryId) {
        InquiryDTO inquiry = inquiryService.replyToInquiry(inquiryId);
        return ResponseEntity.ok(inquiry);
    }

    /**
     * Delete inquiry
     * DELETE /api/inquiries/{inquiryId}
     */
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);
        return ResponseEntity.noContent().build();
    }
}

