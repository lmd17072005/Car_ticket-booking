package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.model.user.Role;

public interface IRoleService
{
    Role findByRoleName(RoleName roleName);
}
