package com.icet.service;

import com.icet.exception.EntityNotFoundException;
import com.icet.model.dto.InquiryCreateDTO;
import com.icet.model.dto.InquiryDTO;
import com.icet.model.entity.Customer;
import com.icet.model.entity.Inquiry;
import com.icet.model.entity.Property;
import com.icet.repository.CustomerRepository;
import com.icet.repository.InquiryRepository;
import com.icet.repository.PropertyRepository;
import com.icet.service.mapper.InquiryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private InquiryMapper inquiryMapper;

    /**
     * Create a new inquiry from customer
     */
    public InquiryDTO createInquiry(InquiryCreateDTO dto) {
        // Validate customer exists
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        // Validate property exists
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        // Create inquiry
        Inquiry inquiry = new Inquiry();
        inquiry.setCustomer(customer);
        inquiry.setProperty(property);
        inquiry.setMessage(dto.getMessage());
        inquiry.setInquiryDate(LocalDate.now());
        inquiry.setIsReplied(false);

        Inquiry saved = inquiryRepository.save(inquiry);

        // Increment property inquiry count
        propertyService.incrementInquiryCount(property.getPropertyId());

        return inquiryMapper.toInquiryDTO(saved);
    }

    /**
     * Get inquiry by ID
     */
    public InquiryDTO getInquiryById(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("Inquiry not found"));
        return inquiryMapper.toInquiryDTO(inquiry);
    }

    /**
     * Get all inquiries for a customer
     */
    public List<InquiryDTO> getInquiriesByCustomer(Long customerId) {
        return inquiryRepository.findByCustomerId(customerId).stream()
                .map(inquiryMapper::toInquiryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all inquiries for a property
     */
    public List<InquiryDTO> getInquiriesByProperty(Long propertyId) {
        return inquiryRepository.findByPropertyId(propertyId).stream()
                .map(inquiryMapper::toInquiryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get unanswered inquiries for a property (for seller)
     */
    public List<InquiryDTO> getUnansweredInquiries(Long propertyId) {
        return inquiryRepository.findByPropertyIdAndIsRepliedFalse(propertyId).stream()
                .map(inquiryMapper::toInquiryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mark inquiry as replied by seller
     */
    public InquiryDTO replyToInquiry(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("Inquiry not found"));

        inquiry.setIsReplied(true);
        Inquiry updated = inquiryRepository.save(inquiry);

        return inquiryMapper.toInquiryDTO(updated);
    }

    /**
     * Delete inquiry
     */
    public void deleteInquiry(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("Inquiry not found"));
        inquiryRepository.delete(inquiry);
    }
}

