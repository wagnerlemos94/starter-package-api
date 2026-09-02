package br.com.digidatasistemas.starterPackage.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Starter Package API",
                version = "1.0.0",
                description = "API REST para autenticação e gerenciamento de usuários, perfis, recursos e permissões.",
                contact = @Contact(
                        name = "Starter Package",
                        url = "https://github.com/wagnerlemos94/starter-package-api"
                ),
                license = @License(name = "Uso interno")
        ),
        servers = @Server(url = "/api", description = "Servidor atual"),
        security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
)
@SecurityScheme(
        name = OpenApiConfig.BEARER_AUTH,
        description = "Informe o JWT retornado por POST /auth/login.",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";

}
