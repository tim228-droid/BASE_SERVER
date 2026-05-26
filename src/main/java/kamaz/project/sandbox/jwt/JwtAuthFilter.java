package kamaz.project.sandbox.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    
    @Value("${JWT_ACCESS_COOKIE_NAME}")
    private String accessTokenCookieName;
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

@Override
protected void doFilterInternal(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {
    
    String token = null;
    Cookie[] cookies = request.getCookies();
    
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (accessTokenCookieName.equals(cookie.getName())) {
                token = cookie.getValue();
                log.debug("Найден cookie с токеном: {}", accessTokenCookieName);
                break;
            }
        }
    }
    
    if (token != null && tokenProvider.validateToken(token)) {
        String username = tokenProvider.getUsernameFromToken(token);
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.info(" Пользователь {} авторизован через JWT", username);
        }
    } else {
        // Не выводим сообщение для public URL
        String uri = request.getRequestURI();
        if (!uri.contains("/auth/register") && !uri.contains("/auth/login") 
            && !uri.contains("/swagger") && !uri.contains("/v3/api-docs")) {
            log.debug(" Токен отсутствует или невалиден для URI: {}", uri);
        }
    }
    
    filterChain.doFilter(request, response);
}
}