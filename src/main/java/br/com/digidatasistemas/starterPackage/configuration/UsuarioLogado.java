package br.com.digidatasistemas.starterPackage.configuration;

import lombok.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Component
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioLogado implements Serializable {

    private UUID idUsuario;
    private String name;
    private String userName;
    private String token;
    private String perfil;
}