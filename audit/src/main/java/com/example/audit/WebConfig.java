package com.example.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
class WebConfig {
    @Bean
    public RouterFunction<ServerResponse> routes(PostHandler postHandler, CommentHandler commentHandler, Notifier notifier, ReactiveUserDetailsService userDetailsService) {
        var postRoutes = route()
                .nest(path(""), () -> route()
                        .GET("", postHandler::all)
                        .POST("", postHandler::create)
                        .build())
                .nest(path("{id}"), () -> route()
                        .GET("", postHandler::get)
                        .PUT("", postHandler::update)
                        .DELETE("", postHandler::delete)
                        .nest(path("comments"), () -> route()
                                .GET("", commentHandler::getByPostId)
                                .POST("", commentHandler::create)
                                .build())
                        .build())
                .build();
        return route()
                .GET("/hello", req -> notifier.send().flatMap((v) -> noContent().build()))
                .path("/posts", () -> postRoutes)
                .GET("/users/{user}", req -> ok().body(userDetailsService.findByUsername(req.pathVariable("user")), UserDetails.class))
                .build();
    }
}