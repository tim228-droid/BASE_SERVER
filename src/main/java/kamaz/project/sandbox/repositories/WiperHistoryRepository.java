package kamaz.project.sandbox.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kamaz.project.sandbox.models.WiperHistory;

@Repository
public interface WiperHistoryRepository extends JpaRepository<WiperHistory, Long> {
}