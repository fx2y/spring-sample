package com.example.audit;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

interface CommentRepository extends R2dbcRepository<Comment, UUID> {
    Flux<Comment> findByPostId(UUID postId);
}
