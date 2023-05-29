package com.bilgeadam.service;

import com.bilgeadam.dto.request.CreateLikeRequestDto;
import com.bilgeadam.dto.response.UserProfileResponseDto;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.exception.PostManagerException;
import com.bilgeadam.manager.IUserProfileManager;
import com.bilgeadam.repository.IDislikeRepository;
import com.bilgeadam.repository.entity.Dislike;
import com.bilgeadam.repository.entity.Post;
import com.bilgeadam.utility.JwtTokenProvider;
import com.bilgeadam.utility.ServiceManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DisLikeService extends ServiceManager<Dislike,String> {
    private final IDislikeRepository dislikeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final IUserProfileManager userProfileManager;
    private final PostService postService;

    public DisLikeService(IDislikeRepository dislikeRepository, JwtTokenProvider jwtTokenProvider, IUserProfileManager userProfileManager, PostService postService) {
        super(dislikeRepository);
        this.dislikeRepository = dislikeRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userProfileManager = userProfileManager;
        this.postService = postService;
    }

    public Boolean dislikePost(CreateLikeRequestDto dto){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new PostManagerException(ErrorType.INVALID_TOKEN);
        }
        UserProfileResponseDto userProfileResponseDto = userProfileManager.findByAuthId(authId.get()).getBody();
        Optional<Dislike> optionalLike = dislikeRepository.findByUserIdAndPostId(userProfileResponseDto.getId(), dto.getPostId());
        if (optionalLike.isEmpty()) {
            //Like like = ILikeMapper.INSTANCE.toLikeFromDto(userProfileResponseDto);
            //like.setPostId(dto.getPostId());
            Dislike like = Dislike.builder()
                    .postId(dto.getPostId())
                    .userId(userProfileResponseDto.getId())
                    .username(userProfileResponseDto.getUsername())
                    .avatar(userProfileResponseDto.getAvatar())
                    .build();
            save(like);
            Optional<Post> post =postService.findById(dto.getPostId());
            if (post.isEmpty()) {
                throw new PostManagerException(ErrorType.USER_NOT_FOUND); // post bulunamadı
            }
            post.get().getLikes().add(like.getId());
            postService.update(post.get());
        }
        else {
            Dislike deletedLike = findById(optionalLike.get().getId()).get(); // like id
            Optional<Post> post =postService.findById(dto.getPostId());
            if (post.isEmpty()) {
                throw new PostManagerException(ErrorType.USER_NOT_FOUND); // post bulunamadı
            }
            post.get().getLikes().remove(optionalLike.get().getId());      //.add(optionalLike.get().getId());
            postService.update(post.get());
            delete(deletedLike);
        }
        return true;
    }
}
