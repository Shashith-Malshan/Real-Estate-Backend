package com.icet.controller;

import com.icet.model.dto.PropertyDealCreateDTO;
import com.icet.model.dto.PropertyDealDTO;
import com.icet.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
public class DealController {

    @Autowired
    private DealService dealService;

    /**
     * Create a new property deal
     * POST /api/deals
     */
    @PostMapping
    public ResponseEntity<PropertyDealDTO> createDeal(@RequestBody PropertyDealCreateDTO dto) {
        PropertyDealDTO deal = dealService.createDeal(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(deal);
    }

    /**
     * Get deal by ID
     * GET /api/deals/{dealId}
     */
    @GetMapping("/{dealId}")
    public ResponseEntity<PropertyDealDTO> getDealById(@PathVariable Long dealId) {
        PropertyDealDTO deal = dealService.getDealById(dealId);
        return ResponseEntity.ok(deal);
    }

    /**
     * Get deals by customer
     * GET /api/customers/{customerId}/deals
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PropertyDealDTO>> getByCustomer(@PathVariable Long customerId) {
        List<PropertyDealDTO> deals = dealService.getDealsByCustomer(customerId);
        return ResponseEntity.ok(deals);
    }

    /**
     * Get deals by property
     * GET /api/properties/{propertyId}/deals
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<PropertyDealDTO>> getByProperty(@PathVariable Long propertyId) {
        List<PropertyDealDTO> deals = dealService.getDealsByProperty(propertyId);
        return ResponseEntity.ok(deals);
    }

    /**
     * Delete deal
     * DELETE /api/deals/{dealId}
     */
    @DeleteMapping("/{dealId}")
    public ResponseEntity<Void> deleteDeal(@PathVariable Long dealId) {
        dealService.deleteDeal(dealId);
        return ResponseEntity.noContent().build();
    }
}

