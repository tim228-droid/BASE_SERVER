package kamaz.project.sandbox.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kamaz.project.sandbox.models.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByResourceAndOperation(String resource, String operation);
}