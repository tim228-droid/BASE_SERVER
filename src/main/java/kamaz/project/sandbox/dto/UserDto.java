package kamaz.project.sandbox.dto;

import java.io.Serializable;
import java.util.Set;
public record UserDto (
        Long id,
        String username,
        String password,
        String role,
        Set<String> permissions
)implements Serializable { }