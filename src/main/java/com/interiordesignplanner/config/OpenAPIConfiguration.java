package com.interiordesignplanner.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {

        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Interior Design Planner API");

        Info information = new Info()
                .title("Interior Design Planner API")
                .version("1.0.0")
                .description(
                        "A RESTful API to help Interior designers manage and organise their clients and projects.")
                .contact(new Contact()
                        .name("Victoria Olusegun")
                        .email("support@interiordesignplanner.com")
                        .url("https://github.com/Vicko657/interior-design-planner-api"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));

        return new OpenAPI().info(information).servers(List.of(server)).tags(List.of(
                new Tag().name("Authentication")
                        .description("User management and authentication operations"),
                new Tag().name("Clients")
                        .description(
                                "Information about the clients"),
                new Tag().name("Projects")
                        .description("Client's project directory"),
                new Tag().name("Rooms")
                        .description("Project's room specification")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().name("bearerAuth").type(SecurityScheme.Type.HTTP).scheme("bearer")
                                .bearerFormat("JWT").description("Enter your JWT Token")));
    }

}
