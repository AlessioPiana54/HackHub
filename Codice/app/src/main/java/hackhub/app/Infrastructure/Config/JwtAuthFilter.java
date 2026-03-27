package hackhub.app.Infrastructure.Config;

import hackhub.app.Application.Utils.IJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro JWT globale: intercetta ogni richiesta, estrae e valida il Bearer token
 * e popola il SecurityContext così Spring Security può applicare le regole di
 * autorizzazione prima che la richiesta raggiunga il controller.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;

    public JwtAuthFilter(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.isTokenValid(token)) {
                String userId = jwtService.extractUserId(token);
                String ruolo = jwtService.extractRuolo(token);

                var authorities = ruolo != null
                        ? List.of(new SimpleGrantedAuthority("ROLE_" + ruolo))
                        : List.<SimpleGrantedAuthority>of();

                var authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
