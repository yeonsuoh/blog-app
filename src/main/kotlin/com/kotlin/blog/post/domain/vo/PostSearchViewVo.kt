package com.kotlin.blog.post.domain.vo

import java.time.LocalDateTime

data class PostSearchViewVo(
    val id: Long,
    val title: String,
    val userNickname: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
)
