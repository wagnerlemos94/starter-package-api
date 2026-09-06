package br.com.digidatasistemas.starterPackage.security;

import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.service.IJwtService;
import br.com.digidatasistemas.starterPackage.service.IUsuarioDetailsService;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final IUsuarioDetailsService userDetailsService;
    private final SecurityErrorResponseWriter errorWriter;

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

            String cpf = jwtService.extractUsername(token);

            if (cpf != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                Usuario usuario =
                        userDetailsService.loadUserByUsername(cpf);

                if (jwtService.isTokenValid(token, usuario)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    usuario,
                                    null,
                                    usuario.getAuthorities());

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                } else {
                    unauthorized(response, request, "Token inválido ou usuário inativo.");
                    return;
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
        } catch (UsernameNotFoundException ex) {

            unauthorized(response, request, "Token inválido ou usuário inativo.");
        } catch (IllegalArgumentException ex) {

            unauthorized(response, request, "Token inválido.");
        }
    }

    private void unauthorized(
            HttpServletResponse response,
            HttpServletRequest request,
            String message) throws IOException {

        SecurityContextHolder.clearContext();
        errorWriter.write(response, request, HttpStatus.UNAUTHORIZED, message);
    }
}
