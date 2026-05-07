package com.icet.service.mapper;

import com.icet.model.dto.InquiryDTO;
import com.icet.model.entity.Inquiry;
import org.springframework.stereotype.Component;

@Component
public class InquiryMapper {

    public InquiryDTO toInquiryDTO(Inquiry inquiry) {
        InquiryDTO dto = new InquiryDTO();
        dto.setInquiryId(inquiry.getInquiryId());
        dto.setCustomerId(inquiry.getCustomer().getCustomerId());
        dto.setPropertyId(inquiry.getProperty().getPropertyId());
        dto.setMessage(inquiry.getMessage());
        dto.setInquiryDate(inquiry.getInquiryDate());
        dto.setIsReplied(inquiry.getIsReplied());
        return dto;
    }

    public Inquiry toInquiryEntity(InquiryDTO dto) {
        Inquiry inquiry = new Inquiry();
        inquiry.setMessage(dto.getMessage());
        inquiry.setInquiryDate(dto.getInquiryDate());
        inquiry.setIsReplied(dto.getIsReplied());
        return inquiry;
    }
}

