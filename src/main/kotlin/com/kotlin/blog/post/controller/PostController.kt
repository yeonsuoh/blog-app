package com.kotlin.blog.post.controller

import com.kotlin.blog.common.annotation.ExistenceCheck
import com.kotlin.blog.common.util.ApiResponse
import com.kotlin.blog.common.util.createResponse
import com.kotlin.blog.post.domain.vo.PostSaveVo
import com.kotlin.blog.post.domain.vo.PostUpdateVo
import com.kotlin.blog.post.dto.request.OrderBy
import com.kotlin.blog.post.dto.request.PostSaveRequest
import com.kotlin.blog.post.dto.request.PostUpdateRequest
import com.kotlin.blog.post.dto.request.SortBy
import com.kotlin.blog.post.dto.request.SortingRequest
import com.kotlin.blog.post.dto.response.PostListResponse
import com.kotlin.blog.post.dto.response.PostResponse
import com.kotlin.blog.post.service.PostService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postService: PostService,
) {

    @GetMapping
    fun findAll(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "sortBy", required = false, defaultValue = "ID") sortBy: String,
        @RequestParam(value = "orderBy", required = false, defaultValue = "DESC") orderBy: String,
    ): ResponseEntity<ApiResponse<Page<PostListResponse>>> {
        val sortingRequest = createSortingRequest(sortBy, orderBy)

        val allPosts = postService.getAllPosts(page, sortingRequest).map { postVo ->
            PostListResponse.voToDto(postVo)
        }

        return createResponse(HttpStatus.OK, data = allPosts)
    }

    @ExistenceCheck
    @GetMapping("/{id}")
    fun findPostById(@PathVariable id: Long): ResponseEntity<ApiResponse<PostResponse>> {
        val postById = postService.getPostById(id)

        val post = PostResponse.voToDto(postById)

        return createResponse(HttpStatus.OK, data = post)
    }

    @PostMapping
    fun createPost(
        @RequestBody @Valid
        request: PostSaveRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        // request를 vo로 변환
        val postSaveVo = PostSaveVo(request.title, request.content, request.userId)

        postService.savePost(postSaveVo)

        return createResponse(HttpStatus.CREATED)
    }

    @ExistenceCheck
    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        postService.deletePostById(id)

        return createResponse(HttpStatus.OK)
    }

    @ExistenceCheck
    @PutMapping("/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @RequestBody @Valid
        request: PostUpdateRequest,
    ):
        ResponseEntity<ApiResponse<Unit>> {
        val postUpdateVo = PostUpdateVo(id, request.title, request.content)

        postService.updatePost(postUpdateVo)

        return createResponse(HttpStatus.OK)
    }

    @GetMapping("/search")
    fun searchPostsByKeyword(
        @RequestParam(value = "keyword") keyword: String,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "sortBy", required = false, defaultValue = "ID") sortBy: String,
        @RequestParam(value = "orderBy", required = false, defaultValue = "DESC") orderBy: String,
    ):
        ResponseEntity<ApiResponse<Page<PostListResponse>>> {
        val sortingRequest = createSortingRequest(sortBy, orderBy)

        val searchedPosts = postService.searchPosts(keyword, page, sortingRequest).map { postVo ->
            PostListResponse.voToDto(postVo)
        }

        return createResponse(HttpStatus.OK, data = searchedPosts)
    }

    private fun createSortingRequest(
        sortBy: String,
        orderBy: String,
    ): SortingRequest {
        val sortByEnum = SortBy.entries.find { it.name.equals(sortBy, ignoreCase = true) }
            ?: throw IllegalArgumentException("잘못된 정렬 기준: $sortBy")
        val orderByEnum = OrderBy.entries.find { it.name.equals(orderBy, ignoreCase = true) }
            ?: throw IllegalArgumentException("잘못된 정렬 순서: $orderBy")

        return SortingRequest(sortByEnum, orderByEnum)
    }
}
