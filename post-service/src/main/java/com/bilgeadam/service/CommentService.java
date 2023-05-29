package com.bilgeadam.service;

import com.bilgeadam.dto.request.CreateCommentRequestDto;
import com.bilgeadam.dto.request.DeleteCommentRequestDto;
import com.bilgeadam.dto.request.DeletePostRequestDto;
import com.bilgeadam.dto.response.UserProfileResponseDto;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.exception.PostManagerException;
import com.bilgeadam.manager.IUserProfileManager;
import com.bilgeadam.repository.ICommentRepository;
import com.bilgeadam.repository.entity.Comment;
import com.bilgeadam.repository.entity.Post;
import com.bilgeadam.utility.JwtTokenProvider;
import com.bilgeadam.utility.ServiceManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService extends ServiceManager<Comment,String> {
    private final ICommentRepository commentRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final IUserProfileManager userProfileManager;


    public CommentService(ICommentRepository commentRepository, JwtTokenProvider jwtTokenProvider, IUserProfileManager userProfileManager ) {
        super(commentRepository);
        this.commentRepository = commentRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userProfileManager = userProfileManager;

    }

   /* public Boolean deleteComment(DeleteCommentRequestDto dto){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new PostManagerException(ErrorType.INVALID_TOKEN);
        }
        Optional<Comment> comment = commentRepository.findById(dto.getCommentId());
        if (comment.isEmpty()) {
            throw new PostManagerException(ErrorType.POST_NOT_FOUND); // comment not found ekle
        }
        // postta silme
        Optional<Post> post = postService.findById(comment.get().getPostId());
        post.get().getComments().remove(comment.get().getId());
        postService.update(post.get());
        // likeda silme

        // sub commment varsa onları silme
        
        delete(comment.get());
        return true;
    }*/
}
