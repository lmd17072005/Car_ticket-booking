package com.ra.base_spring_boot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:Car Ticket Booking}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        // Determine service name based on port
        String serviceName;
        String serviceDescription;

        if ("8081".equals(serverPort)) {
            serviceName = "Booking Service";
            serviceDescription = "APIs for ticket booking, schedules, stations, routes, and user management";
        } else if ("8082".equals(serverPort)) {
            serviceName = "Payment Service";
            serviceDescription = "APIs for payment processing and VNPay integration";
        } else {
            serviceName = "API Service";
            serviceDescription = "REST APIs for Car Ticket Booking System";
        }

        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " - " + serviceName)
                        .version("1.0.0")
                        .description(serviceDescription + "\n\n"
                                + "## Authentication\n"
                                + "Most endpoints require JWT authentication. Follow these steps:\n"
                                + "1. Login via `/api/v1/auth/login` to get JWT token\n"
                                + "2. Click the **Authorize** button (🔒) at the top\n"
                                + "3. Enter: `Bearer <your_token>`\n"
                                + "4. Click **Authorize** and **Close**\n\n"
                                + "## Test Accounts\n"
                                + "- Admin: `admin@gmail.com` / `123456`\n"
                                + "- User: Create via `/api/v1/auth/register`")
                        .contact(new Contact()
                                .name("lmd17072005")
                                .email("lmd17072005@gmail.com")
                                .url("https://github.com/lmd17072005/Car_ticket-booking"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description(serviceName + " - Local Development Server")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("Enter JWT token in format: Bearer <token>\n\n"
                                                + "Example: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")));
    }
}