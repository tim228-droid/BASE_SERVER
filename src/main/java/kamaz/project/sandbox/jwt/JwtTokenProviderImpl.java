package kamaz.project.sandbox.jwt;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kamaz.project.sandbox.enums.TokenType;
import kamaz.project.sandbox.models.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Override
    public Token generateAccessToken(Map<String, Object> extraClaims, 
    long duration, TemporalUnit durationType, UserDetails user) {
        String username = user.getUsername();
        
        Map<String, Object> claims = new HashMap<>();
        if (extraClaims != null) {
            claims.putAll(extraClaims);
        }
        
        String role = user.getAuthorities().stream()
            .findFirst()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .orElse("ROLE_USER");
        
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        claims.put("role", role);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plus(duration, durationType);
        
        log.info(" Создан ACCESS токен для пользователя: {}, роль: {}, истекает: {}", username, role, expiryDate);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(toDate(now))
                .setExpiration(toDate(expiryDate))
                .signWith(decodeSecretKey(jwtSecret), SignatureAlgorithm.HS256)
                .compact();

        return new Token(TokenType.ACCESS, token, expiryDate, false, null);
    }

    @Override
    public Token generateRefreshToken(long duration, 
    TemporalUnit durationType, UserDetails user) {
        String username = user.getUsername();
        
        Map<String, Object> claims = new HashMap<>();
        String role = user.getAuthorities().stream()
            .findFirst()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .orElse("ROLE_USER");
        
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        claims.put("role", role);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plus(duration, durationType);
        
        log.info(" Создан REFRESH токен для пользователя: {}, истекает: {}", username, expiryDate);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(toDate(now))
                .setExpiration(toDate(expiryDate))
                .signWith(decodeSecretKey(jwtSecret), SignatureAlgorithm.HS256)
                .compact();

        return new Token(TokenType.REFRESH, token, expiryDate, false, null);
    }

    @Override
    public boolean validateToken(String tokenValue) {
        if(tokenValue == null) {
            log.debug(" Токен отсутствует");
            return false;
        }
        try {
            Claims claims = extractAllClaims(tokenValue);
            String username = claims.getSubject();
            boolean isValid = !claims.getExpiration().before(new Date());
            
            if (isValid) {
                log.debug(" Токен валиден для пользователя: {}", username);
            } else {
                log.warn(" Токен истёк для пользователя: {}", username);
            }
            return isValid;
        } catch(JwtException e) {
            log.warn(" Невалидный токен: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getUsernameFromToken(String tokenValue) {
        String username = extractClaim(tokenValue, Claims::getSubject);
        log.debug(" Из токена получен пользователь: {}", username);
        return username;
    }
    
    @Override
    public LocalDateTime getExpiryDateFromToken(String tokenValue) {
        return toLocalDateTime(extractClaim(tokenValue, Claims::getExpiration));
    }
    
    private Key decodeSecretKey(String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(decodedKey);
    }
    
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(decodeSecretKey(jwtSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Date toDate(LocalDateTime localDateTime) {
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        return Date.from(localDateTime.toInstant(zoneOffset));
    }
    
    private LocalDateTime toLocalDateTime(Date date) {
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        return date.toInstant().atOffset(zoneOffset).toLocalDateTime();
    }
}