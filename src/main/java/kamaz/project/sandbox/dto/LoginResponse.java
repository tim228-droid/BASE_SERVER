package kamaz.project.sandbox.dto;

public record LoginResponse(
        boolean isLogged,
        String roles
) {
}
