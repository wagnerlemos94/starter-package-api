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

import static br.com.digidatasistemas.starterPackage.constrants.Constrants.*;

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
                    unauthorized(response, request, MSG_TOKEN_USUARIO_INVALIDO);
                    return;
                }
            }

            filterChain.doFilter(request, response);


        } catch (ExpiredJwtException ex) {

            unauthorized(response, request, MSG_TOKEN_EXPIRADO);

        } catch (MalformedJwtException ex) {

            unauthorized(response, request, MSG_TOKEN_INVALIDO);

        } catch (UnsupportedJwtException ex) {

            unauthorized(response, request, MSG_TOKEN_NAO_SUPORTADO);

        } catch (SignatureException ex) {

            unauthorized(response, request, MSG_TOKEN_ASSINATURA_INVALIDA);

        } catch (JwtException ex) {

            unauthorized(response, request, MSG_FALHA_AUTENTICACAO);
        } catch (UsernameNotFoundException ex) {

            unauthorized(response, request, MSG_TOKEN_USUARIO_INVALIDO);
        } catch (IllegalArgumentException ex) {

            unauthorized(response, request, MSG_TOKEN_INVALIDO);
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
