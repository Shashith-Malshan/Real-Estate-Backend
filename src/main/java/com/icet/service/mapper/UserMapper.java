package com.icet.service.mapper;

import com.icet.model.dto.UserRegistrationDTO;
import com.icet.model.dto.UserResponseDTO;
import com.icet.model.entity.User;
import com.icet.model.entity.UserRole;
import com.icet.repository.CustomerRepository;
import com.icet.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserMapper {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public User toUserEntity(UserRegistrationDTO dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setContact(dto.getContact());
        user.setNic(dto.getNic());
        user.setRegDate(LocalDate.now());
        return user;
    }

    public UserResponseDTO toUserResponseDTO(User user, UserRole userRole) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setContact(user.getContact());
        dto.setNic(user.getNic());

        if (userRole != null && userRole.getRole() != null) {
            dto.setActiveRoleId(userRole.getRole().getRoleId());
            dto.setRoleName(userRole.getRole().getName());

            // Populate sellerId if the active role is SELLER
            if ("SELLER".equals(userRole.getRole().getName())) {
                sellerRepository.findByUserRoleUserRoleId(userRole.getUserRoleId())
                        .ifPresent(seller -> dto.setSellerId(seller.getSellerId()));
            }

            // Populate customerId if the active role is BUYER
            if ("BUYER".equals(userRole.getRole().getName())) {
                customerRepository.findByUserRoleUserRoleId(userRole.getUserRoleId())
                        .ifPresent(customer -> dto.setCustomerId(customer.getCustomerId()));
            }
        }

        return dto;
    }
}
