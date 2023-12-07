package com.kotlin.blog.common.security.jwt

import com.kotlin.blog.common.security.configuration.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenUtil(
    jwtProperties: JwtProperties,
) {

    private val secretKey = Keys.hmacShaKeyFor(
        // 주어진 바이트 배열을 기반으로 HMAC-SHA 알고리즘에 사용될 비밀키를 생성. 비밀키는 서명 생성과 검증에 사용됨.
        jwtProperties.key.toByteArray(), // jwtProperties의 key 프로퍼티 값을 바이트 배열로 변환
    )

    fun generate(
        userDetails: UserDetails,
        expirationDate: Date,
    ): String =
        Jwts.builder()
            .claims()
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(expirationDate)
            .and()
            .signWith(secretKey)
            .compact()

    fun isValid(token: String, userDetails: UserDetails): Boolean {
        val email = extractEmail(token)

        return userDetails.username == email && !isExpired(token)
    }

    fun extractEmail(token: String): String? =
        getAllClaims(token)
            .subject

    fun isExpired(token: String): Boolean = // 만료 시간이 현재 시간 이전이면 만료된 토큰
        getAllClaims(token)
            .expiration
            .before(Date(System.currentTimeMillis()))

    private fun getAllClaims(token: String): Claims {
        val parser = Jwts.parser()
            .verifyWith(secretKey)
            .build()

        return parser
            .parseSignedClaims(token)
            .payload
    }
}
