package com.icet.service.impl;

import com.icet.exception.EntityNotFoundException;
import com.icet.model.dto.PropertyDealCreateDTO;
import com.icet.model.dto.PropertyDealDTO;
import com.icet.model.entity.Customer;
import com.icet.model.entity.Property;
import com.icet.model.entity.PropertyDeal;
import com.icet.repository.CustomerRepository;
import com.icet.repository.PropertyDealRepository;
import com.icet.repository.PropertyRepository;
import com.icet.service.DealService;
import com.icet.service.mapper.PropertyDealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DealServiceImpl implements DealService {

	@Autowired
	private PropertyDealRepository dealRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private PropertyDealMapper dealMapper;

	@Override
	public PropertyDealDTO createDeal(PropertyDealCreateDTO dto) {
		Customer customer = customerRepository.findById(dto.getCustomerId())
				.orElseThrow(() -> new EntityNotFoundException("Customer not found"));

		Property property = propertyRepository.findById(dto.getPropertyId())
				.orElseThrow(() -> new EntityNotFoundException("Property not found"));

		if (dealRepository.existsByProperty_PropertyIdAndCustomer_CustomerId(dto.getPropertyId(), dto.getCustomerId())) {
			throw new IllegalArgumentException("Deal already exists for this property and customer");
		}

		PropertyDeal deal = new PropertyDeal();
		deal.setProperty(property);
		deal.setCustomer(customer);
		deal.setAgreedTotalAmount(dto.getAgreedTotalAmount());
		deal.setDealDate(LocalDate.now());

		PropertyDeal saved = dealRepository.save(deal);

		return dealMapper.toDealDTO(saved);
	}

	@Override
	public PropertyDealDTO getDealById(Long dealId) {
		PropertyDeal deal = dealRepository.findById(dealId)
				.orElseThrow(() -> new EntityNotFoundException("Deal not found"));
		return dealMapper.toDealDTO(deal);
	}

	@Override
	public List<PropertyDealDTO> getDealsByCustomer(Long customerId) {
		return dealRepository.findByCustomer_CustomerId(customerId).stream()
				.map(dealMapper::toDealDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<PropertyDealDTO> getDealsByProperty(Long propertyId) {
		return dealRepository.findByProperty_PropertyId(propertyId).stream()
				.map(dealMapper::toDealDTO)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteDeal(Long dealId) {
		PropertyDeal deal = dealRepository.findById(dealId)
				.orElseThrow(() -> new EntityNotFoundException("Deal not found"));
		dealRepository.delete(deal);
	}
}


