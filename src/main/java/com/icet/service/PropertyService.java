package com.icet.service;

import com.icet.exception.EntityNotFoundException;
import com.icet.model.dto.PropertyCreateDTO;
import com.icet.model.dto.PropertyDTO;
import com.icet.model.entity.CommercialProperty;
import com.icet.model.entity.Land;
import com.icet.model.entity.Property;
import com.icet.model.entity.PropertyCategory;
import com.icet.model.entity.PropertyImage;
import com.icet.model.entity.ResidentialProperty;
import com.icet.model.entity.Seller;
import com.icet.repository.CommercialPropertyRepository;
import com.icet.repository.LandRepository;
import com.icet.repository.PropertyCategoryRepository;
import com.icet.repository.PropertyImageRepository;
import com.icet.repository.PropertyRepository;
import com.icet.repository.ResidentialPropertyRepository;
import com.icet.repository.SellerRepository;
import com.icet.service.mapper.PropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyCategoryRepository propertyCategoryRepository;

    @Autowired
    private ResidentialPropertyRepository residentialPropertyRepository;

    @Autowired
    private CommercialPropertyRepository commercialPropertyRepository;

    @Autowired
    private LandRepository landRepository;

    @Autowired
    private PropertyImageRepository propertyImageRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private PropertyMapper propertyMapper;

    /**
     * Get all properties
     */
    public List<PropertyDTO> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get property by ID
     */
    public PropertyDTO getPropertyById(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + propertyId));
        return convertToDTO(property);
    }

    /**
     * Search properties by district
     */
    public List<PropertyDTO> searchByDistrict(String district) {
        return propertyRepository.findByDistrictContaining(district).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search properties by location
     */
    public List<PropertyDTO> searchByLocation(String location) {
        return propertyRepository.findByLocationContaining(location).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search properties by category
     */
    public List<PropertyDTO> searchByCategory(Long categoryId) {
        return propertyRepository.findByPropertyCategory_PropertyCategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search with multiple filters
     */
    public List<PropertyDTO> searchProperties(String district, Long categoryId) {
        return propertyRepository.findByDistrictAndPropertyCategory_PropertyCategoryId(district, categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get properties by seller ID
     */
    public List<PropertyDTO> getPropertiesBySeller(Long sellerId) {
        // Verify seller exists
        sellerRepository.findById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with ID: " + sellerId));
        return propertyRepository.findBySeller_SellerId(sellerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new property with category-specific details
     */
    public PropertyDTO createProperty(PropertyCreateDTO dto) {
        // Validate category exists
        PropertyCategory category = propertyCategoryRepository.findById(dto.getPropertyCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Property category not found"));

        // Create base property
        Property property = new Property();
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setLocation(dto.getLocation());
        property.setDistrict(dto.getDistrict());
        property.setPropertyCategory(category);
        property.setVisitCount(0);
        property.setInquiryCount(0);

        // Associate seller if sellerId provided
        if (dto.getSellerId() != null) {
            sellerRepository.findById(dto.getSellerId()).ifPresent(property::setSeller);
        }

        Property savedProperty = propertyRepository.save(property);

        // Create category-specific property
        String categoryName = category.getName().toLowerCase();
        if (categoryName.contains("residential")) {
            ResidentialProperty residential = new ResidentialProperty();
            residential.setProperty(savedProperty);
            residential.setPrice(dto.getPrice());
            residential.setBedroomCount(dto.getBedroomCount());
            residential.setBathroomCount(dto.getBathroomCount());
            residential.setType(dto.getResidentialType());
            residential.setStatus(dto.getResidentialStatus());
            residentialPropertyRepository.save(residential);
        } else if (categoryName.contains("commercial")) {
            CommercialProperty commercial = new CommercialProperty();
            commercial.setProperty(savedProperty);
            commercial.setPrice(dto.getPrice());
            commercial.setFloorSize(dto.getFloorSize());
            commercial.setType(dto.getCommercialType());
            commercial.setStatus(dto.getCommercialStatus());
            commercialPropertyRepository.save(commercial);
        } else if (categoryName.contains("land")) {
            Land land = new Land();
            land.setProperty(savedProperty);
            land.setPlotCount(dto.getPlotCount());
            land.setUnitPrice(dto.getUnitPrice());
            landRepository.save(land);
        }

        // Add images if provided
        if (dto.getImagePaths() != null && !dto.getImagePaths().isEmpty()) {
            for (String imagePath : dto.getImagePaths()) {
                PropertyImage image = new PropertyImage();
                image.setProperty(savedProperty);
                image.setImagePath(imagePath);
                propertyImageRepository.save(image);
            }
        }

        return convertToDTO(savedProperty);
    }

    /**
     * Update property
     */
    public PropertyDTO updateProperty(Long propertyId, PropertyDTO dto) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setLocation(dto.getLocation());
        property.setDistrict(dto.getDistrict());

        Property updated = propertyRepository.save(property);
        return convertToDTO(updated);
    }

    /**
     * Delete property
     */
    public void deleteProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        // Delete related images
        List<PropertyImage> images = propertyImageRepository.findByProperty_PropertyId(propertyId);
        propertyImageRepository.deleteAll(images);

        // Delete category-specific records
        residentialPropertyRepository.findByProperty_PropertyId(propertyId).ifPresent(residentialPropertyRepository::delete);
        commercialPropertyRepository.findByProperty_PropertyId(propertyId).ifPresent(commercialPropertyRepository::delete);
        landRepository.findByProperty_PropertyId(propertyId).ifPresent(landRepository::delete);

        // Delete base property
        propertyRepository.delete(property);
    }

    /**
     * Increment visit count for a property
     */
    public void incrementVisitCount(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        property.setVisitCount(property.getVisitCount() + 1);
        propertyRepository.save(property);
    }

    /**
     * Increment inquiry count for a property
     */
    public void incrementInquiryCount(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        property.setInquiryCount(property.getInquiryCount() + 1);
        propertyRepository.save(property);
    }

    /**
     * Helper method to convert Property to DTO with all type-specific details
     */
    private PropertyDTO convertToDTO(Property property) {
        PropertyCategory category = property.getPropertyCategory();
        ResidentialProperty residential = residentialPropertyRepository.findByProperty_PropertyId(property.getPropertyId()).orElse(null);
        CommercialProperty commercial = commercialPropertyRepository.findByProperty_PropertyId(property.getPropertyId()).orElse(null);
        Land land = landRepository.findByProperty_PropertyId(property.getPropertyId()).orElse(null);

        return propertyMapper.toPropertyDTO(property, category, residential, commercial, land);
    }
}

