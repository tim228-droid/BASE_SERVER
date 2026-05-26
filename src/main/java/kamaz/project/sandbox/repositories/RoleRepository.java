package kamaz.project.sandbox.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kamaz.project.sandbox.models.Role;
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
