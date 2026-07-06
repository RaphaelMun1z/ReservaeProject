package ticket_service.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;

public class WebConfig {
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
            .allowCredentials(true)
            .maxAge(3600);
    }
}
