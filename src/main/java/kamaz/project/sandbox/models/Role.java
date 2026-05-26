package kamaz.project.sandbox.models;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @OneToMany(mappedBy = "role")
    private Set<User> users;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission", joinColumns = 
    @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    @Override
    public String getAuthority() {
        return this.name.toUpperCase();
    }
}
