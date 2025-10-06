package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.model.user.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Long>
{

    Optional<Role> findByRoleName(RoleName roleName);

}
