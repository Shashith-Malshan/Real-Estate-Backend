package com.icet.service.mapper;

import com.icet.model.dto.PropertyDealDTO;
import com.icet.model.entity.PropertyDeal;
import org.springframework.stereotype.Component;

@Component
public class PropertyDealMapper {

    public PropertyDealDTO toDealDTO(PropertyDeal deal) {
        PropertyDealDTO dto = new PropertyDealDTO();
        dto.setDealId(deal.getDealId());
        dto.setPropertyId(deal.getProperty().getPropertyId());
        dto.setCustomerId(deal.getCustomer().getCustomerId());
        dto.setAgreedTotalAmount(deal.getAgreedTotalAmount());
        dto.setDealDate(deal.getDealDate());
        return dto;
    }

    public PropertyDeal toDealEntity(PropertyDealDTO dto) {
        PropertyDeal deal = new PropertyDeal();
        deal.setAgreedTotalAmount(dto.getAgreedTotalAmount());
        deal.setDealDate(dto.getDealDate());
        return deal;
    }
}

