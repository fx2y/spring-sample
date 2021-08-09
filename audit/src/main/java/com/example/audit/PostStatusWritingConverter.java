package com.example.audit;

import org.springframework.data.r2dbc.convert.EnumWriteSupport;

class PostStatusWritingConverter extends EnumWriteSupport<Post.Status> {
}
