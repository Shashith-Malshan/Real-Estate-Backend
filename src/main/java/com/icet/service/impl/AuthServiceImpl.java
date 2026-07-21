package com.icet.service.impl;

import com.icet.model.dto.RoleSwitchRequestDTO;
import com.icet.model.dto.UserLoginDTO;
import com.icet.model.dto.UserRegistrationDTO;
import com.icet.model.dto.UserResponseDTO;
import com.icet.model.entity.*;
import com.icet.repository.*;
import com.icet.service.AuthService;
import com.icet.service.mapper.UserMapper;
import com.icet.security.JwtTokenProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final UserCredentialsRepository userCredentialsRepository;

    private final UserRoleRepository userRoleRepository;

    private final RoleRepository roleRepository;

    private final CustomerRepository customerRepository;

    private final SellerRepository sellerRepository;

    private final SellerTypeRepository sellerTypeRepository;

    private final UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder;
    
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                       UserCredentialsRepository userCredentialsRepository,
                       UserRoleRepository userRoleRepository,
                       RoleRepository roleRepository,
                       CustomerRepository customerRepository,
                       SellerRepository sellerRepository,
                       SellerTypeRepository sellerTypeRepository,
                       UserMapper userMapper,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.sellerRepository = sellerRepository;
        this.sellerTypeRepository = sellerTypeRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public UserResponseDTO register(UserRegistrationDTO dto) {
        if (userCredentialsRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.findByNic(dto.getNic()).isPresent()) {
            throw new IllegalArgumentException("NIC already registered");
        }

        Role role = resolveRole(dto.getRoleId());

        User user = userMapper.toUserEntity(dto);
        User savedUser = userRepository.save(user);

        UserCredentials credentials = new UserCredentials();
        credentials.setUsername(dto.getUsername());
        credentials.setPassword(passwordEncoder.encode(dto.getPassword()));
        credentials.setUser(savedUser);
        userCredentialsRepository.save(credentials);

        UserRole savedUserRole = createUserRole(savedUser, role, true);

        if (isBuyerRole(role)) {
            Role sellerRole = createOrLoadRoleByName("SELLER");
            UserRole savedSellerRole = createUserRole(savedUser, sellerRole, false);
            ensureCustomerProfile(savedUserRole);
            ensureSellerProfile(savedSellerRole);
        } else if (isSellerRole(role)) {
            Role buyerRole = createOrLoadRoleByName("BUYER");
            UserRole savedBuyerRole = createUserRole(savedUser, buyerRole, false);
            ensureCustomerProfile(savedBuyerRole);
            ensureSellerProfile(savedUserRole);
        } else if (isAdminRole(role)) {
            ensureAdminRoleAssignment(savedUserRole);
        }

        UserResponseDTO responseDTO = userMapper.toUserResponseDTO(savedUser, savedUserRole);
        String token = jwtTokenProvider.generateToken(savedUser.getEmail(), savedUserRole.getRole().getRoleId(), savedUserRole.getRole().getName());
        responseDTO.setToken(token);
        return responseDTO;
    }

    @Override
    public List<Long> getAvailableRoleIds(Long userId) {
        return userRoleRepository.findByUserUserId(userId).stream()
                .map(userRole -> userRole.getRole().getRoleId())
                .distinct()
                .toList();
    }

    @Override
    public UserResponseDTO switchActiveRole(RoleSwitchRequestDTO dto) {
        if (dto == null || dto.getUserId() == null || dto.getRoleId() == null) {
            throw new IllegalArgumentException("User ID and role ID are required");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Role targetRole = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found for ID: " + dto.getRoleId()));

        UserRole targetUserRole = userRoleRepository.findByUserUserIdAndRoleRoleId(user.getUserId(), targetRole.getRoleId())
                .orElseGet(() -> createUserRole(user, targetRole, false));

        activateUserRole(user.getUserId(), targetUserRole);

        if (isBuyerRole(targetRole)) {
            ensureCustomerProfile(targetUserRole);
        } else if (isSellerRole(targetRole)) {
            ensureSellerProfile(targetUserRole);
        }

        UserResponseDTO responseDTO = userMapper.toUserResponseDTO(user, targetUserRole);
        String token = jwtTokenProvider.generateToken(user.getEmail(), targetUserRole.getRole().getRoleId(), targetUserRole.getRole().getName());
        responseDTO.setToken(token);
        return responseDTO;
    }

    private Role resolveRole(Long roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("Invalid role ID");
        }

        return roleRepository.findByName(mapFrontendRoleName(roleId))
                .orElseThrow(() -> new IllegalArgumentException("Role not found for ID: " + roleId));
    }

    private String mapFrontendRoleName(Long roleId) {
        return switch (roleId.intValue()) {
            case 1 -> "BUYER";
            case 2 -> "ADMIN";
            case 3 -> "SELLER";
            default -> throw new IllegalArgumentException("Invalid role ID");
        };
    }

    @Override
    public UserResponseDTO login(UserLoginDTO dto) {
        UserCredentials credentials = userCredentialsRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.getPassword(), credentials.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = credentials.getUser();
        if (user == null) {
            throw new IllegalStateException("Credentials are not linked to a user");
        }

        UserRole activeUserRole = userRoleRepository.findByUserUserIdAndIsLastLoginTrue(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No active role found"));

        UserResponseDTO responseDTO = userMapper.toUserResponseDTO(user, activeUserRole);
        String token = jwtTokenProvider.generateToken(user.getEmail(), activeUserRole.getRole().getRoleId(), activeUserRole.getRole().getName());
        responseDTO.setToken(token);
        return responseDTO;
    }

    private UserRole createUserRole(User user, Role role, boolean active) {
        return userRoleRepository.findByUserUserIdAndRoleRoleId(user.getUserId(), role.getRoleId())
                .map(existingUserRole -> {
                    existingUserRole.setIsLastLogin(active);
                    return userRoleRepository.save(existingUserRole);
                })
                .orElseGet(() -> {
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(role);
                    userRole.setIsLastLogin(active);
                    return userRoleRepository.save(userRole);
                });
    }

    private void activateUserRole(Long userId, UserRole targetUserRole) {
        List<UserRole> userRoles = userRoleRepository.findByUserUserId(userId);
        for (UserRole userRole : userRoles) {
            boolean shouldBeActive = userRole.getUserRoleId().equals(targetUserRole.getUserRoleId());
            if (!Boolean.valueOf(shouldBeActive).equals(userRole.getIsLastLogin())) {
                userRole.setIsLastLogin(shouldBeActive);
                userRoleRepository.save(userRole);
            }
        }
    }

    private Role createOrLoadRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
    }

    private void ensureCustomerProfile(UserRole userRole) {
        if (userRole == null) {
            return;
        }

        customerRepository.findByUserRole(userRole)
                .orElseGet(() -> {
                    Customer customer = new Customer();
                    customer.setUserRole(userRole);
                    return customerRepository.save(customer);
                });
    }

    private void ensureSellerProfile(UserRole userRole) {
        if (userRole == null) {
            return;
        }

        sellerRepository.findByUserRoleUserRoleId(userRole.getUserRoleId())
                .orElseGet(() -> {
                    Seller seller = new Seller();
                    seller.setUserRole(userRole);
                    seller.setSellerType(getOrCreateDefaultSellerType());
                    return sellerRepository.save(seller);
                });
    }

    private SellerType getOrCreateDefaultSellerType() {
        return sellerTypeRepository.findByType("INDIVIDUAL")
                .orElseGet(() -> {
                    SellerType sellerType = new SellerType();
                    sellerType.setType("INDIVIDUAL");
                    return sellerTypeRepository.save(sellerType);
                });
    }

    private void ensureAdminRoleAssignment(UserRole userRole) {
        if (userRole != null) {
            userRole.setIsLastLogin(true);
            userRoleRepository.save(userRole);
        }
    }

    private boolean isBuyerRole(Role role) {
        return role != null && "BUYER".equals(role.getName());
    }

    private boolean isSellerRole(Role role) {
        return role != null && "SELLER".equals(role.getName());
    }

    private boolean isAdminRole(Role role) {
        return role != null && "ADMIN".equals(role.getName());
    }
}

