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
@Table(name = "wiper_history")
public class WiperHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "wiper_id")
    private Wiper wiper;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private Boolean oldStatus;
    private Boolean newStatus;
    
    private String action;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    private LocalDateTime createdAt;
}