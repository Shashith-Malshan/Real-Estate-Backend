package com.icet.service;

import com.icet.model.dto.UserLoginDTO;
import com.icet.model.dto.UserRegistrationDTO;
import com.icet.model.dto.UserResponseDTO;
import com.icet.model.entity.Customer;
import com.icet.model.entity.Role;
import com.icet.model.entity.User;
import com.icet.model.entity.UserCredentials;
import com.icet.model.entity.UserRole;
import com.icet.repository.CustomerRepository;
import com.icet.repository.RoleRepository;
import com.icet.repository.UserCredentialsRepository;
import com.icet.repository.UserRepository;
import com.icet.repository.UserRoleRepository;
import com.icet.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * User registration: creates User, UserCredentials, UserRole, and Customer (if buyer)
     */
    public UserResponseDTO register(UserRegistrationDTO dto) {
        // Validate username doesn't already exist
        if (userCredentialsRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Validate email doesn't already exist
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Validate NIC doesn't already exist
        if (userRepository.findByNic(dto.getNic()).isPresent()) {
            throw new IllegalArgumentException("NIC already registered");
        }

        // Get the role
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID"));

        // Create and save User
        User user = userMapper.toUserEntity(dto);
        User savedUser = userRepository.save(user);

        // Create and save UserCredentials (password hashed)
        UserCredentials credentials = new UserCredentials();
        credentials.setUsername(dto.getUsername());
        credentials.setPassword(passwordEncoder.encode(dto.getPassword()));
        userCredentialsRepository.save(credentials);

        // Create and save UserRole (with is_last_login = true)
        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(role);
        userRole.setIsLastLogin(true);
        UserRole savedUserRole = userRoleRepository.save(userRole);

        // If role is buyer (R003), create Customer profile
        if ("R003".equals(role.getName())) {
            Customer customer = new Customer();
            customer.setUserRole(savedUserRole);
            customerRepository.save(customer);
        }

        // Return response DTO
        return userMapper.toUserResponseDTO(savedUser, savedUserRole);
    }

    /**
     * User login: validates credentials and returns UserResponseDTO
     */
    public UserResponseDTO login(UserLoginDTO dto) {
        // Find credentials by username
        UserCredentials credentials = userCredentialsRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        // Validate password
        if (!passwordEncoder.matches(dto.getPassword(), credentials.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Find the user (assuming user_id is stored or derived from credentials)
        // For now, we'll search by username in user_credentials and link back
        // This requires a relationship; for now, search all users and match
        User user = userRepository.findByEmail(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Get the active UserRole (is_last_login = true)
        UserRole activeUserRole = userRoleRepository.findByUserIdAndIsLastLoginTrue(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No active role found"));

        // Return response DTO
        return userMapper.toUserResponseDTO(user, activeUserRole);
    }
}

