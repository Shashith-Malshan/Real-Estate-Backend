package com.icet.controller;

import com.icet.model.dto.VisitCreateDTO;
import com.icet.model.dto.VisitDTO;
import com.icet.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
@CrossOrigin
public class VisitController {

    @Autowired
    private VisitService visitService;

    /**
     * Schedule a new property visit
     * POST /api/visits
     */
    @PostMapping
    public ResponseEntity<VisitDTO> scheduleVisit(@RequestBody VisitCreateDTO dto) {
        VisitDTO visit = visitService.scheduleVisit(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(visit);
    }

    /**
     * Get visit by ID
     * GET /api/visits/{visitId}
     */
    @GetMapping("/{visitId}")
    public ResponseEntity<VisitDTO> getVisitById(@PathVariable Long visitId) {
        VisitDTO visit = visitService.getVisitById(visitId);
        return ResponseEntity.ok(visit);
    }

    /**
     * Get visits by customer
     * GET /api/visits/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<VisitDTO>> getByCustomer(@PathVariable Long customerId) {
        List<VisitDTO> visits = visitService.getVisitsByCustomer(customerId);
        return ResponseEntity.ok(visits);
    }

    /**
     * Get visits by property
     * GET /api/properties/{propertyId}/visits
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<VisitDTO>> getByProperty(@PathVariable Long propertyId) {
        List<VisitDTO> visits = visitService.getVisitsByProperty(propertyId);
        return ResponseEntity.ok(visits);
    }

    /**
     * Get pending visits for property
     * GET /api/properties/{propertyId}/visits/pending
     */
    @GetMapping("/property/{propertyId}/pending")
    public ResponseEntity<List<VisitDTO>> getPending(@PathVariable Long propertyId) {
        List<VisitDTO> visits = visitService.getPendingVisits(propertyId);
        return ResponseEntity.ok(visits);
    }

    /**
     * Mark visit as completed
     * PUT /api/visits/{visitId}/complete
     */
    @PutMapping("/{visitId}/complete")
    public ResponseEntity<VisitDTO> completeVisit(@PathVariable Long visitId) {
        VisitDTO visit = visitService.completeVisit(visitId);
        return ResponseEntity.ok(visit);
    }

    /**
     * Cancel visit
     * DELETE /api/visits/{visitId}
     */
    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> cancelVisit(@PathVariable Long visitId) {
        visitService.cancelVisit(visitId);
        return ResponseEntity.noContent().build();
    }
}

