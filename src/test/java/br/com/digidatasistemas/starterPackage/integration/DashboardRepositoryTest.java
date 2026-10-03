package br.com.digidatasistemas.starterPackage.integration;

import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.repository.PerfilRepository;
import br.com.digidatasistemas.starterPackage.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class DashboardRepositoryTest {
    @Autowired private UsuarioRepository usuarios;
    @Autowired private PerfilRepository perfis;

    @Test
    void deveRetornarZerosSemCadastros() {
        var resumo = usuarios.buscarDadosDashboard();
        assertEquals(0L, resumo.getTotal());
        assertEquals(0L, resumo.getAtivos());
        assertEquals(0L, resumo.getInativos());
    }

    @Test
    void deveContarTotalAtivosEInativosNaOrdemCorreta() {
        var perfil = perfis.save(Perfil.builder().nome("Gestor").chave("GESTOR").ativo(true).build());
        perfis.save(Perfil.builder().nome("Antigo").chave("ANTIGO").ativo(false).build());
        usuarios.save(Usuario.builder().cpf("00000000535").nome("Maria").senha("hash").ativo(true).perfil(perfil).build());
        usuarios.save(Usuario.builder().cpf("00000000108").nome("João").senha("hash").ativo(true).perfil(perfil).build());
        usuarios.save(Usuario.builder().cpf("00000000280").nome("Ana").senha("hash").ativo(false).perfil(perfil).build());

        var resumo = usuarios.buscarDadosDashboard();
        assertEquals(3L, resumo.getTotal());
        assertEquals(2L, resumo.getAtivos());
        assertEquals(1L, resumo.getInativos());
        var resumoPerfil = perfis.buscarDadosDashboard();
        assertEquals(2L, resumoPerfil.getTotal());
        assertEquals(1L, resumoPerfil.getAtivos());
        assertEquals(1L, resumoPerfil.getInativos());
    }
}
