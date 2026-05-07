package com.icet.controller;

import com.icet.model.dto.PropertyCreateDTO;
import com.icet.model.dto.PropertyDTO;
import com.icet.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    /**
     * Get all properties
     * GET /api/properties
     */
    @GetMapping
    public ResponseEntity<List<PropertyDTO>> getAllProperties() {
        List<PropertyDTO> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    /**
     * Get property by ID
     * GET /api/properties/{propertyId}
     */
    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long propertyId) {
        PropertyDTO property = propertyService.getPropertyById(propertyId);
        return ResponseEntity.ok(property);
    }

    /**
     * Search properties by district
     * GET /api/properties/search/district?district=Colombo
     */
    @GetMapping("/search/district")
    public ResponseEntity<List<PropertyDTO>> searchByDistrict(@RequestParam String district) {
        List<PropertyDTO> properties = propertyService.searchByDistrict(district);
        return ResponseEntity.ok(properties);
    }

    /**
     * Search properties by location
     * GET /api/properties/search/location?location=Negombo
     */
    @GetMapping("/search/location")
    public ResponseEntity<List<PropertyDTO>> searchByLocation(@RequestParam String location) {
        List<PropertyDTO> properties = propertyService.searchByLocation(location);
        return ResponseEntity.ok(properties);
    }

    /**
     * Search properties by category
     * GET /api/properties/search/category?categoryId=1
     */
    @GetMapping("/search/category")
    public ResponseEntity<List<PropertyDTO>> searchByCategory(@RequestParam Long categoryId) {
        List<PropertyDTO> properties = propertyService.searchByCategory(categoryId);
        return ResponseEntity.ok(properties);
    }

    /**
     * Advanced search with filters
     * GET /api/properties/search?district=Colombo&categoryId=1
     */
    @GetMapping("/search")
    public ResponseEntity<List<PropertyDTO>> searchProperties(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Long categoryId) {
        
        if (district != null && categoryId != null) {
            List<PropertyDTO> properties = propertyService.searchProperties(district, categoryId);
            return ResponseEntity.ok(properties);
        } else if (district != null) {
            List<PropertyDTO> properties = propertyService.searchByDistrict(district);
            return ResponseEntity.ok(properties);
        } else if (categoryId != null) {
            List<PropertyDTO> properties = propertyService.searchByCategory(categoryId);
            return ResponseEntity.ok(properties);
        } else {
            return getAllProperties();
        }
    }

    /**
     * Create new property
     * POST /api/properties
     */
    @PostMapping
    public ResponseEntity<PropertyDTO> createProperty(@RequestBody PropertyCreateDTO dto) {
        PropertyDTO property = propertyService.createProperty(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(property);
    }

    /**
     * Update property
     * PUT /api/properties/{propertyId}
     */
    @PutMapping("/{propertyId}")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long propertyId,
            @RequestBody PropertyDTO dto) {
        PropertyDTO updated = propertyService.updateProperty(propertyId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete property
     * DELETE /api/properties/{propertyId}
     */
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long propertyId) {
        propertyService.deleteProperty(propertyId);
        return ResponseEntity.noContent().build();
    }
}

