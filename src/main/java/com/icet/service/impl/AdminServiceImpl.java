package com.icet.service.impl;

import com.icet.model.dto.SystemMetricsDTO;
import com.icet.model.dto.UserResponseDTO;
import com.icet.model.entity.*;
import com.icet.repository.*;
import com.icet.service.AdminService;
import com.icet.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyDealRepository dealRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserRole activeRole = userRoleRepository.findByUserUserIdAndIsLastLoginTrue(user.getUserId())
                            .orElse(null);
                    return userMapper.toUserResponseDTO(user, activeRole);
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        UserRole activeRole = userRoleRepository.findByUserUserIdAndIsLastLoginTrue(user.getUserId())
                .orElse(null);
        return userMapper.toUserResponseDTO(user, activeRole);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<UserRole> userRoles = userRoleRepository.findByUserUserId(userId);

        for (UserRole userRole : userRoles) {
            Long userRoleId = userRole.getUserRoleId();

            sellerRepository.findByUserRoleUserRoleId(userRoleId).ifPresent(seller -> {
                List<Property> properties = propertyRepository.findBySeller_SellerId(seller.getSellerId());
                for (Property property : properties) {
                    Long propertyId = property.getPropertyId();
                    dealRepository.deleteAll(dealRepository.findByProperty_PropertyId(propertyId));
                    inquiryRepository.deleteAll(inquiryRepository.findByProperty_PropertyId(propertyId));
                    visitRepository.deleteAll(visitRepository.findByProperty_PropertyId(propertyId));
                }
                propertyRepository.deleteAll(properties);
                sellerRepository.delete(seller);
            });

            buyerRepository.findByUserRole_UserRoleId(userRoleId).ifPresent(buyer -> {
                buyerRepository.delete(buyer);
            });

            customerRepository.findByUserRoleUserRoleId(userRoleId).ifPresent(customer -> {
                Long customerId = customer.getCustomerId();
                dealRepository.deleteAll(dealRepository.findByCustomer_CustomerId(customerId));
                inquiryRepository.deleteAll(inquiryRepository.findByCustomer_CustomerId(customerId));
                visitRepository.deleteAll(visitRepository.findByCustomer_CustomerId(customerId));
                customerRepository.delete(customer);
            });
        }

        userCredentialsRepository.findByUserUserId(userId)
                .ifPresent(userCredentialsRepository::delete);

        userRoleRepository.deleteAll(userRoles);

        userRepository.delete(user);
    }

    @Override
    public SystemMetricsDTO getSystemMetrics() {
        long totalUsers = userRepository.count();
        long totalProperties = propertyRepository.count();
        long totalDeals = dealRepository.count();

        List<PropertyDeal> deals = dealRepository.findAll();
        double platformRevenue = deals.stream()
                .map(PropertyDeal::getAgreedTotalAmount)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        double actualRevenue = platformRevenue * 0.01;

        return new SystemMetricsDTO(totalUsers, totalProperties, totalDeals, actualRevenue);
    }
}

