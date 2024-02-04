package com.apigateway;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

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

    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            org.springframework.http.server.reactive.ServerHttpRequest request = ctx.getRequest();
            if (CorsUtils.isCorsRequest(request)) {
                org.springframework.http.server.reactive.ServerHttpResponse response = ctx.getResponse();
                org.springframework.http.HttpHeaders headers = response.getHeaders();
                headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
                headers.add("Access-Control-Max-Age", "3600");
                headers.add("Access-Control-Allow-Headers", "Authorization, Content-Type");
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }
}
