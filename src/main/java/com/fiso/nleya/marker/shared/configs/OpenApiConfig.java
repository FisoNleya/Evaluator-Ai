package com.fiso.nleya.marker.shared.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        email = "nleyafiso@gmail.com",
                        url = "www.linkedin.com/in/fiso-nleya"
                ),
                description = "Service via REST API that allows accessors to create assessments, upload marking schemes, assesses take assessment and the platform evaluates using AI tools ",
                title = "Evaluator AI API ",
                version = "1.0",
                license = @License(
                        name = "dev Fiso",
                        url = "www.linkedin.com/in/fiso-nleya"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "https://fisonleya/avaluatorai"
                )
        }
)
public class OpenApiConfig {
}

