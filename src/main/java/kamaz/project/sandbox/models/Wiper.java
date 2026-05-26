package kamaz.project.sandbox.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wipers")
public class Wiper {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String brand;
    
    @Column(nullable = false)
    private String model;
    
    @Column(nullable = false)
    private Integer length;
    
    @Column(nullable = false)
    private String material;
    
    private Boolean isBroken;
    
    @Column(columnDefinition = "TEXT")
    private String breakReason;
    
    @Column(columnDefinition = "TEXT")
    private String anomalyDescription;
    
    private boolean anomaly;  // НОВОЕ ПОЛЕ
    
    @Column(nullable = false)
    private Boolean isActive;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    
    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;
    
    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
}