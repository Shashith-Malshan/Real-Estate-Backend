package com.icet.repository;

import com.icet.model.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserUserId(Long userId);

    Optional<UserRole> findByUserUserIdAndIsLastLoginTrue(Long userId);

    Optional<UserRole> findByUserUserIdAndRoleRoleId(Long userId, Long roleId);

    List<UserRole> findByRoleRoleId(Long roleId);
}
