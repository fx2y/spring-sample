package com.example.audit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

interface PostRepository extends R2dbcRepository<Post, UUID> {
    @Query("SELECT * FROM posts WHERE title LIKE :title")
    Flux<Post> findByTitleContains(String title);

    Flux<PostSummary> findByTitleLike(String title, Pageable pageable);
}