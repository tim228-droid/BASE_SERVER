package kamaz.project.sandbox.services.impl;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kamaz.project.sandbox.dto.LoginRequest;
import kamaz.project.sandbox.dto.LoginResponse;
import kamaz.project.sandbox.dto.RegisterRequest;
import kamaz.project.sandbox.dto.UserLoggedDto;
import kamaz.project.sandbox.jwt.JwtTokenProvider;
import kamaz.project.sandbox.mapper.UserMapper;
import kamaz.project.sandbox.models.Role;
import kamaz.project.sandbox.models.Token;
import kamaz.project.sandbox.models.User;
import kamaz.project.sandbox.repositories.RoleRepository;
import kamaz.project.sandbox.repositories.TokenRepository;
import kamaz.project.sandbox.repositories.UserRepository;
import kamaz.project.sandbox.services.AuthService;
import kamaz.project.sandbox.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final CookieUtil cookieUtil;

    private static final long ACCESS_DURATION_MINUTES = 5;
    private static final long REFRESH_DURATION_DAYS = 7;

    // LOGIN
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request, String oldAccess, String oldRefresh) {
        log.info("Логин: {}", request.username());

        // 1. Ищем пользователя по username
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Неверный логин или пароль"));

        // 2. Проверяем пароль
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Неверный логин или пароль");
        }

        // 3. Деактивируем все старые токены этого пользователя (если нужна одноразовость)
        tokenRepository.findAll().stream()
                .filter(t -> t.getUser() != null && t.getUser().getId().equals(user.getId()))
                .forEach(t -> {
                    t.setDisabled(true);
                    tokenRepository.save(t);
                });

        // 4. Генерируем НОВЫЕ токены
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .build();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());

        Token accessToken = tokenProvider.generateAccessToken(
                claims,
                ACCESS_DURATION_MINUTES,
                ChronoUnit.MINUTES,
                userDetails
        );

        Token refreshToken = tokenProvider.generateRefreshToken(
                REFRESH_DURATION_DAYS,
                ChronoUnit.DAYS,
                userDetails
        );

        refreshToken.setUser(user);
        tokenRepository.save(refreshToken);

        // 5. Отправляем cookies
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(
                accessToken.getValue(),
                ACCESS_DURATION_MINUTES * 60
        ).toString());
        headers.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(
                refreshToken.getValue(),
                REFRESH_DURATION_DAYS * 24 * 60 * 60
        ).toString());

        log.info("Успешный вход: {} (роль {})", user.getUsername(), user.getRole().getName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new LoginResponse(true, user.getRole().getName()));
    }

    // REGISTER
    @Override
    @Transactional
    public ResponseEntity<?> register(RegisterRequest request) {
        log.info("Регистрация: {}", request.username());

        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body("{\"error\":\"Пользователь уже существует\"}");
        }

        Role role = roleRepository.findByName(request.role().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();

        userRepository.save(user);
        log.info("Пользователь {} сохранён в БД", user.getUsername());

        // Генерируем токены для нового пользователя
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .build();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());

        Token accessToken = tokenProvider.generateAccessToken(
                claims,
                ACCESS_DURATION_MINUTES,
                ChronoUnit.MINUTES,
                userDetails
        );

        Token refreshToken = tokenProvider.generateRefreshToken(
                REFRESH_DURATION_DAYS,
                ChronoUnit.DAYS,
                userDetails
        );

        refreshToken.setUser(user);
        tokenRepository.save(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(
                accessToken.getValue(),
                ACCESS_DURATION_MINUTES * 60
        ).toString());
        headers.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(
                refreshToken.getValue(),
                REFRESH_DURATION_DAYS * 24 * 60 * 60
        ).toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body("{\"message\":\"Пользователь зарегистрирован\", \"role\":\"" + role.getName() + "\"}");
    }

    // REFRESH
    @Override
    public ResponseEntity<LoginResponse> refresh(String refreshTokenValue) {
        // полная логика refresh (оставь как было)
        return ResponseEntity.badRequest().build();
    }

    // LOGOUT
    @Override
    public ResponseEntity<LoginResponse> logout(String access, String refresh) {
        if (refresh != null) {
            tokenRepository.findAll().stream()
                    .filter(t -> t.getValue().equals(refresh))
                    .findFirst()
                    .ifPresent(token -> {
                        token.setDisabled(true);
                        tokenRepository.save(token);
                    });
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookieUtil.deleteAccessTokenCookie().toString());
        headers.add(HttpHeaders.SET_COOKIE, cookieUtil.deleteRefreshTokenCookie().toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new LoginResponse(false, null));
    }

    @Override
    public UserLoggedDto getUserLoggedInfo() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.userToUserLoggedDto(user);
    }
}