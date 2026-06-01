package inventory_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Inventory Service [MatchPass]")
                .version("v0.0.1")
                .description("O projeto MatchPass tem como seu domínio central a \"venda de ingressos para partidas de futebol\". Entre os diferenciais presentes, é possível listar: foco na experiência do usuário, preocupação com escalabilidade e resiliência, modularização para permitir realizar manutenções e adições de features de maneira facilitada.")
                .termsOfService("https://github.com/RaphaelMun1z/MatchPassProject")
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("https://github.com/RaphaelMun1z/MatchPassProject")
                )
            );
    }
}
