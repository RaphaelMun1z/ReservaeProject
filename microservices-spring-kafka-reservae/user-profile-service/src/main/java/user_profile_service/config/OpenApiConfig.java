package user_profile_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;

@OpenAPIDefinition(info =
@Info(title = "User Profile Microservice API",
    version = "v0.0.1",
    description = "Garantia de integridade referencial rigorosa para credenciais de acesso, dados do usuário e permissões."))
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components())
            .info(new io.swagger.v3.oas.models.info.Info()
                .title("User Profile Microservice API")
                .version("v1.0.0")
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("https://github.com/RaphaelMun1z/ReservaeProject")
                )
            );
    }
}
