package kamaz.project.sandbox.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @Schema(description = "Имя пользователя", example = "timlol")
    @NotBlank(message = "Имя пользователя обязательно")
    String username,
    
    @Schema(description = "Пароль", example = "timlox")
    @NotBlank(message = "Пароль обязателен")
    String password,
    
    @Schema(description = "Роль (ADMIN, MANAGER, USER)", example = "ADMIN")
    @NotBlank(message = "Роль обязательна")
    String role
) {}