package br.com.digidatasistemas.starterPackage.integration;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMigrationTest {

    @Test
    void deveRenomearColunasPreservandoDadosERelacionamento() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:usuario-migration;MODE=PostgreSQL")) {
            try (var statement = connection.createStatement()) {
                statement.execute("CREATE TABLE perfil (id UUID PRIMARY KEY)");
                // Mesmo esquema anterior à V7, com a ordem DEFAULT/PRIMARY KEY aceita pelo H2.
                statement.execute("""
                        CREATE TABLE usuario (
                            id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                            cpf VARCHAR(11) UNIQUE NOT NULL,
                            password VARCHAR(255) NOT NULL,
                            name VARCHAR(150) NOT NULL,
                            profile_id UUID NOT NULL,
                            active BOOLEAN NOT NULL DEFAULT TRUE,
                            CONSTRAINT fk_users_profile FOREIGN KEY (profile_id) REFERENCES perfil (id)
                        )
                        """);
                statement.execute("INSERT INTO perfil VALUES ('00000000-0000-0000-0000-000000000001')");
                statement.execute("""
                        INSERT INTO usuario (cpf, name, password, active, profile_id)
                        VALUES ('00000000535', 'Maria', 'hash-existente', false,
                                '00000000-0000-0000-0000-000000000001')
                        """);

                ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/migration/V7__renomear_colunas_usuario.sql"));

                try (var result = statement.executeQuery("SELECT nome, senha, ativo FROM usuario u JOIN perfil p ON p.id = u.perfil_id")) {
                    assertTrue(result.next());
                    assertEquals("Maria", result.getString("nome"));
                    assertEquals("hash-existente", result.getString("senha"));
                    assertFalse(result.getBoolean("ativo"));
                    assertFalse(result.next());
                }
                assertThrows(java.sql.SQLException.class, () -> statement.executeQuery("SELECT name FROM usuario"));
                assertThrows(java.sql.SQLException.class, () -> statement.execute("DELETE FROM perfil"));
            }
        }
    }
}
