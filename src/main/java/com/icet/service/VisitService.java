package com.icet.service;

import com.icet.model.dto.VisitCreateDTO;
import com.icet.model.dto.VisitDTO;

import java.util.List;

public interface VisitService {

    VisitDTO scheduleVisit(VisitCreateDTO dto);

    VisitDTO getVisitById(Long visitId);

    List<VisitDTO> getVisitsByCustomer(Long customerId);

    List<VisitDTO> getVisitsByProperty(Long propertyId);

    List<VisitDTO> getPendingVisits(Long propertyId);

    VisitDTO completeVisit(Long visitId);

    void cancelVisit(Long visitId);
}
