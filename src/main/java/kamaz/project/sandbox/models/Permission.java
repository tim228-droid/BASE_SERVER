package kamaz.project.sandbox.models;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "permissions", uniqueConstraints = 
{@UniqueConstraint(columnNames = {"resource", "operation"})})
public class Permission implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String resource;

    @Column(nullable = false)
    private String operation;
    
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;
    
    public Permission(String resource, String operation) {
        this.resource = resource;
        this.operation = operation;
    }

  

    @Override
    public String getAuthority() {
        return String.format(
            "%s:%s", 
            resource.toUpperCase(), 
        operation.toUpperCase());
    }
    
}