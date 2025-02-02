package org.saeta.ingemmetback.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("INGEMMET-SERVICE API")
                        .description("API para autenticación con JWT")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Sergio")
                                .email("sergio.sotomayor@saeta.pe")));
    }

}