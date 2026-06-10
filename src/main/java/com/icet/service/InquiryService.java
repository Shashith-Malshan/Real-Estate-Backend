package com.icet.service;

import com.icet.model.dto.InquiryCreateDTO;
import com.icet.model.dto.InquiryDTO;

import java.util.List;

public interface InquiryService {

    InquiryDTO createInquiry(InquiryCreateDTO dto);

    InquiryDTO getInquiryById(Long inquiryId);

    List<InquiryDTO> getInquiriesByCustomer(Long customerId);

    List<InquiryDTO> getInquiriesByProperty(Long propertyId);

    List<InquiryDTO> getUnansweredInquiries(Long propertyId);

    InquiryDTO replyToInquiry(Long inquiryId);

    void deleteInquiry(Long inquiryId);
}
