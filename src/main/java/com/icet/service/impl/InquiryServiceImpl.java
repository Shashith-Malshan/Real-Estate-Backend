package com.icet.service.impl;

import com.icet.exception.EntityNotFoundException;
import com.icet.model.dto.InquiryCreateDTO;
import com.icet.model.dto.InquiryDTO;
import com.icet.model.entity.Customer;
import com.icet.model.entity.Inquiry;
import com.icet.model.entity.Property;
import com.icet.repository.CustomerRepository;
import com.icet.repository.InquiryRepository;
import com.icet.repository.PropertyRepository;
import com.icet.service.InquiryService;
import com.icet.service.PropertyService;
import com.icet.service.mapper.InquiryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InquiryServiceImpl implements InquiryService {

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

    @Override
    public InquiryDTO createInquiry(InquiryCreateDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        Inquiry inquiry = new Inquiry();
        inquiry.setCustomer(customer);
        inquiry.setProperty(property);
        inquiry.setMessage(dto.getMessage());
        inquiry.setInquiryDate(LocalDate.now());
        inquiry.setIsReplied(false);

        Inquiry saved = inquiryRepository.save(inquiry);

        propertyService.incrementInquiryCount(property.getPropertyId());

        return inquiryMapper.toInquiryDTO(saved);
    }

    @Override
    public InquiryDTO getInquiryById(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("Inquiry not found"));
        return inquiryMapper.toInquiryDTO(inquiry);
    }

    @Override
    public List<InquiryDTO> getInquiriesByCustomer(Long customerId) {
        return inquiryRepository.findByCustomer_CustomerId(customerId).stream()
                .map(inquiryMapper::toInquiryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InquiryDTO> getInquiriesByProperty(Long propertyId) {
        return inquiryRepository.findByProperty_PropertyId(propertyId).stream()
                .map(inquiryMapper::toInquiryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InquiryDTO> getUnansweredInquiries(Long propertyId) {
        return inquiryRepository.findByProperty_PropertyIdAndIsRepliedFalse(propertyId).stream()
                .map(inquiryMapper::toInquiryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InquiryDTO replyToInquiry(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("Inquiry not found"));

        inquiry.setIsReplied(true);
        Inquiry updated = inquiryRepository.save(inquiry);

        return inquiryMapper.toInquiryDTO(updated);
    }

    @Override
    public void deleteInquiry(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("Inquiry not found"));
        inquiryRepository.delete(inquiry);
    }
}

