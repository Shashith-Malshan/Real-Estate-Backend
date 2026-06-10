package com.icet.service;

import com.icet.model.dto.PropertyDealCreateDTO;
import com.icet.model.dto.PropertyDealDTO;

import java.util.List;

public interface DealService {

    PropertyDealDTO createDeal(PropertyDealCreateDTO dto);

    PropertyDealDTO getDealById(Long dealId);

    List<PropertyDealDTO> getDealsByCustomer(Long customerId);

    List<PropertyDealDTO> getDealsByProperty(Long propertyId);

    void deleteDeal(Long dealId);
}
