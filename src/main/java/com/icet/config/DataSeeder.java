package com.icet.config;

import com.icet.model.entity.Role;
import com.icet.repository.RoleRepository;
import com.icet.model.entity.SellerType;
import com.icet.model.entity.PropertyCategory;
import com.icet.repository.SellerTypeRepository;
import com.icet.repository.PropertyCategoryRepository;
import com.icet.model.entity.User;
import com.icet.model.entity.UserCredentials;
import com.icet.model.entity.UserRole;
import com.icet.repository.UserRepository;
import com.icet.repository.UserCredentialsRepository;
import com.icet.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final SellerTypeRepository sellerTypeRepository;
    private final PropertyCategoryRepository propertyCategoryRepository;
    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final UserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    public DataSeeder(RoleRepository roleRepository,
                      SellerTypeRepository sellerTypeRepository,
                      PropertyCategoryRepository propertyCategoryRepository,
                      UserRepository userRepository,
                      UserCredentialsRepository userCredentialsRepository,
                      UserRoleRepository userRoleRepository,
                      BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.sellerTypeRepository = sellerTypeRepository;
        this.propertyCategoryRepository = propertyCategoryRepository;
        this.userRepository = userRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${APP_ADMIN_PASSWORD:}")
    private String adminPassword;

    @Value("${APP_ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${APP_ADMIN_EMAIL:admin@example.com}")
    private String adminEmail;

    @Override
    public void run(String... args) {
        logger.info("Starting data seeding...");

        seedRole("BUYER");
        seedRole("ADMIN");
        seedRole("SELLER");

        seedSellerType("INDIVIDUAL");
        seedSellerType("COMPANY");

        seedPropertyCategory("RESIDENTIAL");
        seedPropertyCategory("COMMERCIAL");
        seedPropertyCategory("LAND");

        seedAdminUserIfRequested();

        logger.info("Data seeding complete.");
    }

    private void seedRole(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            logger.info("Seeded role={}", roleName);
        } else {
            logger.debug("Role {} already exists, skipping", roleName);
        }
    }

    private void seedSellerType(String typeName) {
        if (sellerTypeRepository.findByType(typeName).isEmpty()) {
            SellerType st = new SellerType();
            st.setType(typeName);
            sellerTypeRepository.save(st);
            logger.info("Seeded seller type={}", typeName);
        } else {
            logger.debug("Seller type {} already exists, skipping", typeName);
        }
    }

    private void seedPropertyCategory(String categoryName) {
        if (propertyCategoryRepository.findByName(categoryName).isEmpty()) {
            PropertyCategory pc = new PropertyCategory();
            pc.setName(categoryName);
            propertyCategoryRepository.save(pc);
            logger.info("Seeded property category={}", categoryName);
        } else {
            logger.debug("Property category {} already exists, skipping", categoryName);
        }
    }

    private void seedAdminUserIfRequested() {
        if (adminPassword == null || adminPassword.isBlank()) {
            logger.debug("No admin password provided (APP_ADMIN_PASSWORD), skipping admin user seeding");
            return;
        }

        // Check if admin user already exists by email
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            logger.info("Admin user with email {} already exists, skipping creation", adminEmail);
            return;
        }

        // Check if username already exists
        if (userCredentialsRepository.findByUsername(adminUsername).isPresent()) {
            logger.warn("Username {} already exists; cannot create admin user", adminUsername);
            return;
        }

        // Create user
        User admin = new User();
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setEmail(adminEmail);
        admin.setContact("0000000000");
        admin.setNic("ADMIN");
        admin.setRegDate(LocalDate.now());
        User savedAdmin = userRepository.save(admin);

        // Create credentials
        UserCredentials credentials = new UserCredentials();
        credentials.setUsername(adminUsername);
        credentials.setPassword(passwordEncoder.encode(adminPassword));
        credentials.setUser(savedAdmin);
        userCredentialsRepository.save(credentials);

        // Attach ADMIN role to user_role
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (adminRole != null) {
            UserRole ur = new UserRole();
            ur.setUser(savedAdmin);
            ur.setRole(adminRole);
            ur.setIsLastLogin(true);
            userRoleRepository.save(ur);
            logger.info("Created admin user {} with role ADMIN", adminUsername);
        } else {
            logger.warn("ADMIN role not found while creating admin user");
        }
    }
}


