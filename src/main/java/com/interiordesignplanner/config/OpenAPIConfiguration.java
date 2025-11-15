package com.interiordesignplanner.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {

        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Interior Design Planner API");

        Contact myContact = new Contact();
        myContact.setName("Victoria Olusegun");
        myContact.setEmail("victoriaolusegun@googlemail.com");

        Info information = new Info()
                .title("Interior Design Planner API")
                .version("1.0")
                .description("A REST API to help Interior designers manage and organise their clients and projects.");

        return new OpenAPI().info(information).servers(List.of(server));
    }

}
