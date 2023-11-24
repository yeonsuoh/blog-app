package com.kotlin.blog.domain.vo

import java.time.LocalDateTime

data class PostSearchViewVo(
    val id: Long,
    val title: String,
    val authorName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
)
