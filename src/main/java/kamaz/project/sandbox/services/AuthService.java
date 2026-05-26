package kamaz.project.sandbox.services;

import org.springframework.http.ResponseEntity;

import kamaz.project.sandbox.dto.LoginRequest;
import kamaz.project.sandbox.dto.LoginResponse;
import kamaz.project.sandbox.dto.RegisterRequest;
import kamaz.project.sandbox.dto.UserLoggedDto;

public interface AuthService {
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest, String accessToken, String refreshToken);

    ResponseEntity<LoginResponse> refresh(String refreshToken);

    ResponseEntity<LoginResponse> logout(String accessToken, String refreshToken);

    UserLoggedDto getUserLoggedInfo();
    ResponseEntity<?> register(RegisterRequest registerRequest);
}