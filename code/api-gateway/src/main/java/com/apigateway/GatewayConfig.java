package com.apigateway;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@Configuration
@EnableDiscoveryClient
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("tournament-route", r -> r.path("/api/tournament/**")
                        .uri("http://tournament-manager:8087"))
                .route("battle-route", r -> r.path("/api/battle/**")
                        .uri("http://battle-manager:8082"))
                .route("account-route", r -> r.path("/api/account/**")
                        .uri("http://account-manager:8086"))
                .route("github-route", r -> r.path("/api/github/**")
                        .uri("http://github-manager:8083"))
                .route("mail-route", r -> r.path("/api/mail/**")
                        .uri("http://mail-service:8085"))
                .build();
    }
}
