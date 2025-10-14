package com.project.bsep.repository;

import com.project.bsep.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository  extends JpaRepository<Permission, Long> {
}
