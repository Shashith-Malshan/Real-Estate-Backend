package com.icet.service.impl;

import com.icet.exception.EntityNotFoundException;
import com.icet.model.dto.VisitCreateDTO;
import com.icet.model.dto.VisitDTO;
import com.icet.model.entity.Customer;
import com.icet.model.entity.Property;
import com.icet.model.entity.Visit;
import com.icet.repository.CustomerRepository;
import com.icet.repository.PropertyRepository;
import com.icet.repository.VisitRepository;
import com.icet.service.PropertyService;
import com.icet.service.VisitService;
import com.icet.service.mapper.VisitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VisitServiceImpl implements VisitService {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private VisitMapper visitMapper;

    @Override
    public VisitDTO scheduleVisit(VisitCreateDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        Visit visit = new Visit();
        visit.setCustomer(customer);
        visit.setProperty(property);
        visit.setVisitPlannedDate(dto.getVisitPlannedDate());
        visit.setIsVisited(false);

        Visit saved = visitRepository.save(visit);

        propertyService.incrementVisitCount(property.getPropertyId());

        return visitMapper.toVisitDTO(saved);
    }

    @Override
    public VisitDTO getVisitById(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));
        return visitMapper.toVisitDTO(visit);
    }

    @Override
    public List<VisitDTO> getVisitsByCustomer(Long customerId) {
        return visitRepository.findByCustomer_CustomerId(customerId).stream()
                .map(visitMapper::toVisitDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitDTO> getVisitsByProperty(Long propertyId) {
        return visitRepository.findByProperty_PropertyId(propertyId).stream()
                .map(visitMapper::toVisitDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitDTO> getPendingVisits(Long propertyId) {
        return visitRepository.findByProperty_PropertyIdAndIsVisitedFalse(propertyId).stream()
                .map(visitMapper::toVisitDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VisitDTO completeVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        visit.setIsVisited(true);
        Visit updated = visitRepository.save(visit);

        return visitMapper.toVisitDTO(updated);
    }

    @Override
    public void cancelVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));
        visitRepository.delete(visit);
    }
}

