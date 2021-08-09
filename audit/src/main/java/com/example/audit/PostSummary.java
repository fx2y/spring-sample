package com.example.audit;

import lombok.Value;

import java.util.UUID;

@Value
class PostSummary {
    UUID id;
    String title;
}
