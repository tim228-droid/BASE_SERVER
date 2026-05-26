package kamaz.project.sandbox.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kamaz.project.sandbox.models.Wiper;

@Repository
public interface WiperRepository extends JpaRepository<Wiper, Long> {
    
    List<Wiper> findByIsActiveTrue();
    
    List<Wiper> findByIsBrokenTrueAndIsActiveTrue();
    
    Optional<Wiper> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT w FROM Wiper w WHERE w.isActive = true AND LOWER(w.brand) LIKE LOWER(CONCAT('%', :brand, '%'))")
    List<Wiper> searchByBrand(@Param("brand") String brand);
    
    long countByIsBrokenTrueAndIsActiveTrue();
    
    long countByIsActiveTrue();
}