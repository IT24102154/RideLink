package com.ridelink.farepayment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI farePaymentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fare & Payment Service API")
                        .description("APIs for fare estimation, payment processing, and payment status updates")
                        .version("v1.0"));
    }
}
