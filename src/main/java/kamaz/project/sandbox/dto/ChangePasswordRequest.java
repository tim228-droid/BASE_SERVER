package kamaz.project.sandbox.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
    @NotBlank(message = "Текущий пароль не может быть пустым")
    String currentPassword,
    
    @NotBlank(message = "Новый пароль не может быть пустым")
    String newPassword,
    
    @NotBlank(message = "Подтверждение пароля не может быть пустым")
    String confirmPassword
) {}