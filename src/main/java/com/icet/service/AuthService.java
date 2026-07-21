package com.icet.service;

import com.icet.model.dto.RoleSwitchRequestDTO;
import com.icet.model.dto.UserLoginDTO;
import com.icet.model.dto.UserRegistrationDTO;
import com.icet.model.dto.UserResponseDTO;

import java.util.List;

public interface AuthService {

    UserResponseDTO register(UserRegistrationDTO dto);

    List<Long> getAvailableRoleIds(Long userId);

    UserResponseDTO switchActiveRole(RoleSwitchRequestDTO dto);

    UserResponseDTO login(UserLoginDTO dto);
}
