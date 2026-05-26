package kamaz.project.sandbox.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kamaz.project.sandbox.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}