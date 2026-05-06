package com.icet.repository;

import com.icet.model.entity.Customer;
import com.icet.model.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByUserRole(UserRole userRole);

	Optional<Customer> findByUserRoleUserRoleId(Long userRoleId);
}

