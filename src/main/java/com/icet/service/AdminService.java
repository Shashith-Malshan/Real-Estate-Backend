package com.icet.service;

import com.icet.model.dto.SystemMetricsDTO;
import com.icet.model.dto.UserResponseDTO;

import java.util.List;

public interface AdminService {

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long userId);

    void deleteUser(Long userId);

    SystemMetricsDTO getSystemMetrics();
}
