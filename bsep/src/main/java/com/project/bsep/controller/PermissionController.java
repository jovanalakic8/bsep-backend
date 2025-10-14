package com.project.bsep.controller;

import com.project.bsep.model.Permission;
import com.project.bsep.service.PermissionService;
import com.project.bsep.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
@Secured("ROLE_ADMIN")
public class PermissionController {

    private final PermissionService permissionService;
    private final UserService userService;

    @Autowired
    public PermissionController(PermissionService permissionService,UserService userService)
    {
        this.permissionService = permissionService;
        this.userService=userService;
    }
    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions(@RequestBody String jwtToken) {
            if (userService.hasAuthority(jwtToken,"SveVezanoZaDozvole")) {
                return ResponseEntity.ok(permissionService.getAllPermissions());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@RequestBody String jwtToken, @PathVariable("id") Long id) {
            if (userService.hasAuthority(jwtToken,"SveVezanoZaDozvole")) {
                Optional<Permission> permission = permissionService.getPermissionById(id);
                return permission.map(value -> ResponseEntity.ok().body(value))
                        .orElseGet(() -> ResponseEntity.notFound().build());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
    }

    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody String jwtToken, @RequestBody Permission permission) {
            if (userService.hasAuthority(jwtToken,"SveVezanoZaDozvole")) {
                Permission createdPermission = permissionService.createPermission(permission);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdPermission);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permission> updatePermission(@RequestBody String jwtToken, @PathVariable("id") Long id, @RequestBody Permission updatedPermission) {

            if (userService.hasAuthority(jwtToken,"SveVezanoZaDozvole")) {
                Permission updated = permissionService.updatePermission(id, updatedPermission);
                if (updated != null) {
                    return ResponseEntity.ok(updated);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@RequestBody String jwtToken, @PathVariable("id") Long id) {

            if (userService.hasAuthority(jwtToken,"SveVezanoZaDozvole")) {
                permissionService.deletePermission(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
    }
}
