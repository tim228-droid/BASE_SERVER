package kamaz.project.sandbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API для управления дворниками")
                .version("1.0")
                .description("CRUD операции для дворников с аномалиями")
                .contact(new Contact()
                    .name("Support")))
            .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
            .schemaRequirement("BearerAuth", new SecurityScheme()
                .name("BearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));
    }
}