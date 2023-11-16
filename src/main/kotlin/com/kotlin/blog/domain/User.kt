package com.kotlin.blog.domain

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Column(unique = true)
    val email: String,

    val password: String,

    @Column(unique = true)
    var nickname: String,

    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true)
    var posts: MutableList<Post> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0, // Id에 null 쓰지 않기
)
