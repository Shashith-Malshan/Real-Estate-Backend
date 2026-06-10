package com.icet.service.impl;

import com.icet.exception.EntityNotFoundException;
import com.icet.model.dto.PropertyCreateDTO;
import com.icet.model.dto.PropertyDTO;
import com.icet.model.entity.CommercialProperty;
import com.icet.model.entity.Land;
import com.icet.model.entity.Property;
import com.icet.model.entity.PropertyCategory;
import com.icet.model.entity.PropertyImage;
import com.icet.model.entity.ResidentialProperty;
import com.icet.repository.CommercialPropertyRepository;
import com.icet.repository.LandRepository;
import com.icet.repository.PropertyCategoryRepository;
import com.icet.repository.PropertyImageRepository;
import com.icet.repository.PropertyRepository;
import com.icet.repository.ResidentialPropertyRepository;
import com.icet.repository.SellerRepository;
import com.icet.service.PropertyService;
import com.icet.service.mapper.PropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

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

    @Override
    public List<PropertyDTO> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyDTO> getLatestProperties(Integer limit, Long categoryId) {
        if (limit == null || limit < 1) {
            limit = 6;
        } else if (limit > 100) {
            limit = 100;
        }

        List<Property> properties;
        if (categoryId != null) {
            properties = propertyRepository.findLatestPropertiesByCategory(limit, categoryId);
        } else {
            properties = propertyRepository.findLatestProperties(limit);
        }

        return properties.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PropertyDTO getPropertyById(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + propertyId));
        return convertToDTO(property);
    }

    @Override
    public List<PropertyDTO> searchByDistrict(String district) {
        return propertyRepository.findByDistrictContaining(district).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyDTO> searchByLocation(String location) {
        return propertyRepository.findByLocationContaining(location).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyDTO> searchByCategory(Long categoryId) {
        return propertyRepository.findByPropertyCategory_PropertyCategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyDTO> searchProperties(String district, Long categoryId) {
        return propertyRepository.findByDistrictAndPropertyCategory_PropertyCategoryId(district, categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyDTO> getPropertiesBySeller(Long sellerId) {
        sellerRepository.findById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with ID: " + sellerId));
        return propertyRepository.findBySeller_SellerId(sellerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PropertyDTO createProperty(PropertyCreateDTO dto) {
        PropertyCategory category = propertyCategoryRepository.findById(dto.getPropertyCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Property category not found"));

        Property property = new Property();
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setLocation(dto.getLocation());
        property.setDistrict(dto.getDistrict());
        property.setPropertyCategory(category);
        property.setVisitCount(0);
        property.setInquiryCount(0);

        if (dto.getSellerId() != null) {
            sellerRepository.findById(dto.getSellerId()).ifPresent(property::setSeller);
        }

        Property savedProperty = propertyRepository.save(property);

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

        List<String> imagesToAdd = dto.getImageDataList() != null && !dto.getImageDataList().isEmpty()
                ? dto.getImageDataList()
                : dto.getImagePaths();

        if (imagesToAdd != null && !imagesToAdd.isEmpty()) {
            for (String imageData : imagesToAdd) {
                PropertyImage image = new PropertyImage();
                image.setProperty(savedProperty);
                image.setImagePath(imageData);
                propertyImageRepository.save(image);
            }
        }

        return convertToDTO(savedProperty);
    }

    @Override
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

    @Override
    public void deleteProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        List<PropertyImage> images = propertyImageRepository.findByProperty_PropertyId(propertyId);
        propertyImageRepository.deleteAll(images);

        residentialPropertyRepository.findByProperty_PropertyId(propertyId).ifPresent(residentialPropertyRepository::delete);
        commercialPropertyRepository.findByProperty_PropertyId(propertyId).ifPresent(commercialPropertyRepository::delete);
        landRepository.findByProperty_PropertyId(propertyId).ifPresent(landRepository::delete);

        propertyRepository.delete(property);
    }

    @Override
    public void incrementVisitCount(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        property.setVisitCount(property.getVisitCount() + 1);
        propertyRepository.save(property);
    }

    @Override
    public void incrementInquiryCount(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        property.setInquiryCount(property.getInquiryCount() + 1);
        propertyRepository.save(property);
    }

    private PropertyDTO convertToDTO(Property property) {
        PropertyCategory category = property.getPropertyCategory();
        ResidentialProperty residential = residentialPropertyRepository.findByProperty_PropertyId(property.getPropertyId()).orElse(null);
        CommercialProperty commercial = commercialPropertyRepository.findByProperty_PropertyId(property.getPropertyId()).orElse(null);
        Land land = landRepository.findByProperty_PropertyId(property.getPropertyId()).orElse(null);
        List<PropertyImage> images = propertyImageRepository.findByProperty_PropertyId(property.getPropertyId());

        return propertyMapper.toPropertyDTO(property, category, residential, commercial, land, images);
    }
}

