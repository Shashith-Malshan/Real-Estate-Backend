package com.icet.service;

import com.icet.exception.EntityNotFoundException;
import com.icet.model.dto.VisitCreateDTO;
import com.icet.model.dto.VisitDTO;
import com.icet.model.entity.Customer;
import com.icet.model.entity.Property;
import com.icet.model.entity.Visit;
import com.icet.repository.CustomerRepository;
import com.icet.repository.PropertyRepository;
import com.icet.repository.VisitRepository;
import com.icet.service.mapper.VisitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VisitService {

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

    /**
     * Schedule a new property visit
     */
    public VisitDTO scheduleVisit(VisitCreateDTO dto) {
        // Validate customer exists
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        // Validate property exists
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        // Create visit
        Visit visit = new Visit();
        visit.setCustomer(customer);
        visit.setProperty(property);
        visit.setVisitPlannedDate(dto.getVisitPlannedDate());
        visit.setIsVisited(false);

        Visit saved = visitRepository.save(visit);

        // Increment property visit count
        propertyService.incrementVisitCount(property.getPropertyId());

        return visitMapper.toVisitDTO(saved);
    }

    /**
     * Get visit by ID
     */
    public VisitDTO getVisitById(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));
        return visitMapper.toVisitDTO(visit);
    }

    /**
     * Get all visits for a customer
     */
    public List<VisitDTO> getVisitsByCustomer(Long customerId) {
        return visitRepository.findByCustomerId(customerId).stream()
                .map(visitMapper::toVisitDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all visits for a property
     */
    public List<VisitDTO> getVisitsByProperty(Long propertyId) {
        return visitRepository.findByPropertyId(propertyId).stream()
                .map(visitMapper::toVisitDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pending visits for a property (not yet visited)
     */
    public List<VisitDTO> getPendingVisits(Long propertyId) {
        return visitRepository.findByPropertyIdAndIsVisitedFalse(propertyId).stream()
                .map(visitMapper::toVisitDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mark visit as completed
     */
    public VisitDTO completeVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        visit.setIsVisited(true);
        Visit updated = visitRepository.save(visit);

        return visitMapper.toVisitDTO(updated);
    }

    /**
     * Cancel visit
     */
    public void cancelVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));
        visitRepository.delete(visit);
    }
}

