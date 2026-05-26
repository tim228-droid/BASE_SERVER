package kamaz.project.sandbox.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kamaz.project.sandbox.models.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

}