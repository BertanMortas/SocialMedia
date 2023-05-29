package com.bilgeadam.controller;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.rabbitmq.model.AuthIdModel;
import com.bilgeadam.repository.entity.Comment;
import com.bilgeadam.repository.entity.Post;
import com.bilgeadam.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bilgeadam.constant.ApiUrls.*;

@RestController
@RequestMapping(POST)
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping(CREATE+"/{token}")
    public ResponseEntity<Post> createPost(@PathVariable String token,@RequestBody CreateNewPostRequestDto dto){
        return ResponseEntity.ok(postService.createPost(token,dto));
    }
    @PostMapping(CREATE+"/rabbitmq"+"/{token}")
    public ResponseEntity<Post> createPostRabbitmq(@PathVariable String token,@RequestBody CreateNewPostRequestDto dto){
        return ResponseEntity.ok(postService.createPostRabbit(token,dto));
    }
    @GetMapping(FIND_ALL)
    public ResponseEntity<List<Post>> findAll(){
        return ResponseEntity.ok(postService.findAll());
    }
    @PostMapping(UPDATE)
    public ResponseEntity<Post> updatePost(String token, String postId, UpdatePostRequestDto dto){
        return ResponseEntity.ok(postService.updatePost(token,postId,dto));
    }
    @PostMapping("/like-post")
    public ResponseEntity<Boolean> likePost(@RequestBody CreateLikeRequestDto dto){
        return ResponseEntity.ok(postService.likePost(dto));
    }
    @PostMapping(DELETE_BY_ID)
    public ResponseEntity<Boolean> deletePost(@RequestBody DeletePostRequestDto dto){
        return ResponseEntity.ok(postService.deletePost(dto));
    }
    @PostMapping("create-post")
    public ResponseEntity<Comment> createComment(@RequestBody CreateCommentRequestDto dto){
        return ResponseEntity.ok(postService.createComment(dto));
    }

}
