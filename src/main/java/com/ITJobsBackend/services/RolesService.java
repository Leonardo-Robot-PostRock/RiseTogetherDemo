package com.ITJobsBackend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ITJobsBackend.domain.Roles;
import com.ITJobsBackend.dto.RolesDTO;
import com.ITJobsBackend.repository.RolesRepository;

@Service
public class RolesService {
    private final RolesRepository rolesRepository;

    public RolesService(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    @Transactional(readOnly = true)
    public List<RolesDTO> getAllRoles() {
        return rolesRepository.findAll().stream()
                .map(role -> RolesDTO.builder()
                        .withId(role.getId())
                        .withName(role.getName())
                        .withDescription(role.getDescription())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public RolesDTO getRoleById(Integer roleId) {
        Optional<Roles> optionalRole = rolesRepository.findById(roleId);
        if (optionalRole.isPresent()) {
            Roles role = optionalRole.get();
            return RolesDTO.builder()
                    .withId(role.getId())
                    .withName(role.getName())
                    .withDescription(role.getDescription())
                    .build();
        } else {
            throw new RuntimeException("Role not found with id: " + roleId);
        }
    }
}
