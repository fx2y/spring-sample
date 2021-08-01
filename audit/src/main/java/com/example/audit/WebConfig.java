package com.example.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
class WebConfig {
    @Bean
    public RouterFunction<ServerResponse> routes(PostHandler postHandler, ReactiveUserDetailsService userDetailsService) {
        var postRoutes = route()
                .GET("", postHandler::all)
                .POST("", postHandler::create)
                .GET("{id}", postHandler::get)
                .PUT("{id}", postHandler::update)
                .DELETE("{id}", postHandler::delete)
                .build();
        return route()
                .path("/posts", () -> postRoutes)
                .GET("/users/{user}", req -> ok().body(userDetailsService.findByUsername(req.pathVariable("user")), UserDetails.class))
                .build();
    }
}