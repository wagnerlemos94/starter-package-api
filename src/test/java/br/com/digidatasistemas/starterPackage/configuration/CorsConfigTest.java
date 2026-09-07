package br.com.digidatasistemas.starterPackage.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorsConfigTest {

    @Test
    void deveCarregarOrigensRemovendoEspacosEDuplicidades() {
        var config = new CorsConfig(
                "http://localhost:3000, https://app.exemplo.com, http://localhost:3000"
        );
        var request = new MockHttpServletRequest("OPTIONS", "/user");

        CorsConfiguration cors = config.corsConfigurationSource()
                .getCorsConfiguration(request);

        assertThat(cors).isNotNull();
        assertThat(cors.getAllowedOrigins()).containsExactly(
                "http://localhost:3000",
                "https://app.exemplo.com"
        );
        assertThat(cors.checkOrigin("https://app.exemplo.com"))
                .isEqualTo("https://app.exemplo.com");
        assertThat(cors.checkOrigin("https://origem-nao-configurada.com"))
                .isNull();
        assertThat(cors.getAllowCredentials()).isTrue();
    }

    @Test
    void deveFalharQuandoNenhumaOrigemFoiConfigurada() {
        assertThatThrownBy(() -> new CorsConfig(" , "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ao menos uma origem");
    }
}
