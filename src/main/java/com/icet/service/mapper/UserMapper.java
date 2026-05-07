package com.icet.service.mapper;

import com.icet.model.dto.UserRegistrationDTO;
import com.icet.model.dto.UserResponseDTO;
import com.icet.model.entity.User;
import com.icet.model.entity.UserRole;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserMapper {

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
        
        if (userRole != null) {
            dto.setActiveRoleId(userRole.getUserRoleId());
            if (userRole.getRole() != null) {
                dto.setRoleName(userRole.getRole().getName());
            }
        }
        
        return dto;
    }
}

