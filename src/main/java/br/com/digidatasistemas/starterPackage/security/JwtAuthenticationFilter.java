package br.com.digidatasistemas.starterPackage.security;

import br.com.digidatasistemas.starterPackage.configuration.UsuarioLogado;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.service.implement.CustomUserDetailsService;
import br.com.digidatasistemas.starterPackage.service.implement.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UsuarioLogado usuarioLogado;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String token = header.substring(7);

            String email = jwtService.extractUsername(token);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                Usuario usuario =
                        userDetailsService.loadUserByUsername(email);

                usuarioLogado.setIdUsuario(usuario.getId());
                usuarioLogado.setToken(token);
                usuarioLogado.setName(usuario.getName());
                usuarioLogado.setUserName(usuario.getUsername());
                usuarioLogado.setPerfil(usuario.getPerfil().getChave());

                if (jwtService.isTokenValid(token, usuario)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    usuario,
                                    null,
                                    usuario.getAuthorities());

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);


        } catch (ExpiredJwtException ex) {

            unauthorized(response, request, "Token expirado.");

        } catch (MalformedJwtException ex) {

            unauthorized(response, request, "Token inválido.");

        } catch (UnsupportedJwtException ex) {

            unauthorized(response, request, "Token não suportado.");

        } catch (SignatureException ex) {

            unauthorized(response, request, "Assinatura do token inválida.");

        } catch (JwtException ex) {

            unauthorized(response, request, "Falha na autenticação.");
        }
    }

    private void unauthorized(
            HttpServletResponse response,
            HttpServletRequest request,
            String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
            {
                "status":401,
                "error":"UNAUTHORIZED",
                "message":"%s",
                "path":"%s"
            }
            """.formatted(message, request.getRequestURI()));
    }
}