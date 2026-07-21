package com.icet.service.mapper;

import com.icet.model.dto.VisitDTO;
import com.icet.model.entity.Visit;
import org.springframework.stereotype.Component;

@Component
public class VisitMapper {

    public VisitDTO toVisitDTO(Visit visit) {
        VisitDTO dto = new VisitDTO();
        dto.setVisitId(visit.getVisitId());
        dto.setCustomerId(visit.getCustomer().getCustomerId());
        dto.setPropertyId(visit.getProperty().getPropertyId());
        dto.setVisitPlannedDate(visit.getVisitPlannedDate());
        dto.setIsVisited(visit.getIsVisited());
        return dto;
    }

    public Visit toVisitEntity(VisitDTO dto) {
        Visit visit = new Visit();
        visit.setVisitPlannedDate(dto.getVisitPlannedDate());
        visit.setIsVisited(dto.getIsVisited());
        return visit;
    }
}

