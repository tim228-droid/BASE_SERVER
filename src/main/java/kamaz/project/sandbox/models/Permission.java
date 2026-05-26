package kamaz.project.sandbox.models;

import org.springframework.security.core.GrantedAuthority;

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
@Table(name = "permissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"resource", "operation"})
})
public class Permission implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String resource;

    @Column(nullable = false)
    private String operation;

    public Permission(String resource, String operation) {
        this.resource = resource;
        this.operation = operation;
    }

    @Override
    public String getAuthority() {
        return resource + ":" + operation;
    }
}