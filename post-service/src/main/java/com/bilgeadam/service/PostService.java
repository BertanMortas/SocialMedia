package com.bilgeadam.service;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.UserProfileResponseDto;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.exception.PostManagerException;
import com.bilgeadam.manager.IUserProfileManager;
import com.bilgeadam.mapper.IPostMapper;
import com.bilgeadam.rabbitmq.model.AuthIdModel;
import com.bilgeadam.rabbitmq.model.GetUserprofileModel;
import com.bilgeadam.rabbitmq.producer.CreatePostProducer;
import com.bilgeadam.repository.IPostRepository;
import com.bilgeadam.repository.entity.Comment;
import com.bilgeadam.repository.entity.Like;
import com.bilgeadam.repository.entity.Post;
import com.bilgeadam.utility.JwtTokenProvider;
import com.bilgeadam.utility.ServiceManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService extends ServiceManager<Post,String> {
    private final IPostRepository postRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final IUserProfileManager userProfileManager;
    private final CreatePostProducer createPostProducer;
    private final LikeService likeService;
    private final CommentService commentService;

    public PostService(IPostRepository postRepository, JwtTokenProvider jwtTokenProvider, IUserProfileManager userProfileManager, CreatePostProducer createPostProducer, LikeService likeService, CommentService commentService) {
        super(postRepository);
        this.postRepository = postRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userProfileManager = userProfileManager;
        this.createPostProducer = createPostProducer;
        this.likeService = likeService;
        this.commentService = commentService;
    }
    public Post createPost(String token, CreateNewPostRequestDto dto){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(token);
        if (authId.isEmpty()) {
            throw new PostManagerException(ErrorType.INVALID_TOKEN);
        }
        UserProfileResponseDto userProfile = userProfileManager.findByAuthId(authId.get()).getBody();// userProfile dönüş tipi
        Post post = IPostMapper.INSTANCE.toPost(dto);
        post.setUserId(userProfile.getId());
        post.setUsername(userProfile.getUsername());
        post.setAvatar(userProfile.getAvatar());
        return save(post);
    }
    public Post createPostRabbit(String token, CreateNewPostRequestDto dto){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(token);
        if (authId.isEmpty()) {
            throw new PostManagerException(ErrorType.INVALID_TOKEN);
        }
        GetUserprofileModel userprofileModel= (GetUserprofileModel) createPostProducer.createPost(AuthIdModel.builder().authId(authId.get()).build()); // mapper a çevir

        Post post = IPostMapper.INSTANCE.toPostFromModel(userprofileModel);
        post.setMediaUrls(dto.getMediaUrls());
        post.setContent(dto.getContent());
        return save(post);
    }
    public Post updatePost(String token,String postId, UpdatePostRequestDto dto){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(token);
        if (authId.isEmpty()) {
            throw new PostManagerException(ErrorType.INVALID_TOKEN);
        }
        UserProfileResponseDto userProfile = userProfileManager.findByAuthId(authId.get()).getBody();
        Optional<Post> post = postRepository.findById(postId);
        if (userProfile.getId().equals(post.get().getUserId())) {
            post.get().getMediaUrls().addAll(dto.getAddMediaUrls());
            post.get().getMediaUrls().removeAll(dto.getRemoveMediaUrls());
            post.get().setContent(dto.getContent());
            return update(post.get());
            //post = IPostMapper.INSTANCE.toPost(dto); hata yapıyor dto nun id isimleri değişmesi lazım
        }
        throw new PostManagerException(ErrorType.POST_NOT_FOUND);
    }
    /**
     * metodunun yapacağı:
     * bir post beğenildiğinde çalıştırılacaktır
     * post beğenildiğinde beğenenin bilgisi gitmelidir
     * ayrıca tek tek hangi postun kimin tarafından beğenildiği bilgisi de like entitysinin tablosunda tutulmalıdır
     * - post beğenmek için giriş yapılmalıdır
     * - post beğenenin bilgileri gereklidir
     *
     * @param dto -> userid, postid
     * @return -> true or false
     */
    public Boolean likePost(CreateLikeRequestDto dto){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new PostManagerException(ErrorType.INVALID_TOKEN);
        }
        UserProfileResponseDto userProfileResponseDto = userProfileManager.findByAuthId(authId.get()).getBody();
        Optional<Like> optionalLike = likeService.findByUserIdAndPostId(userProfileResponseDto.getId(), dto.getPostId());
        if (optionalLike.isEmpty()) {
            //Like like = ILikeMapper.INSTANCE.toLikeFromDto(userProfileResponseDto);
            //like.setPostId(dto.getPostId());
            Like like = Like.builder()
                    .postId(dto.getPostId())
                    .userId(userProfileResponseDto.getId())
                    .username(userProfileResponseDto.getUsername())
                    .avatar(userProfileResponseDto.getAvatar())
                    .build();
            likeService.save(like);
            Optional<Post> post = findById(dto.getPostId());
            if (post.isEmpty()) {
                throw new PostManagerException(ErrorType.USER_NOT_FOUND); // post bulunamadı
            }
            post.get().getLikes().add(like.getId());
            update(post.get());
        }
        else {
            Like deletedLike = likeService.findById(optionalLike.get().getId()).get(); // like id
            Optional<Post> post = findById(dto.getPostId());
            if (post.isEmpty()) {
                throw new PostManagerException(ErrorType.USER_NOT_FOUND); // post bulunamadı
            }
            post.get().getLikes().remove(optionalLike.get().getId());      //.add(optionalLike.get().getId());
            update(post.get());
            likeService.delete(deletedLike);
        }
        return true;
    }
    public Comment createComment(CreateCommentRequestDto dto){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new PostManagerException(ErrorType.INVALID_TOKEN);
        }
        UserProfileResponseDto userProfile = userProfileManager.findByAuthId(authId.get()).getBody();
        Optional<Post> post = postRepository.findById(dto.getPostId());
        if (post.isEmpty()) {
            throw new PostManagerException(ErrorType.POST_NOT_FOUND);
        }
        Comment comment = Comment.builder()
                .userId(userProfile.getId())
                .username(userProfile.getUsername())
                .comment(dto.getComment())
                .postId(dto.getPostId())
                .build();
        commentService.save(comment);
        if (dto.getCommentId() != null) {
            Optional<Comment> commentOptional = commentService.findById(dto.getCommentId());
            if (commentOptional.isEmpty()){
                //TODO exeption yaz
                throw new RuntimeException("bu yorum bulunamadı");
            }
            commentOptional.get().getSubCommentIds().add(comment.getId());
            commentService.update(commentOptional.get());
        }
        post.get().getComments().add(comment.getId());
        update(post.get());
        return comment;
    }
    public Boolean deletePost(DeletePostRequestDto dto){
        Optional<Long> authId = jwtTokenProvider.getIdFromToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new PostManagerException(ErrorType.INVALID_TOKEN);
        }
        Optional<Post> post = findById(dto.getPostId());
        UserProfileResponseDto userProfileResponseDto = userProfileManager.findByAuthId(authId.get()).getBody();
        if (post.get().getUserId().equals(userProfileResponseDto.getId())) {
            post.get().getLikes().forEach(x -> likeService.deleteById(x));
            deleteById(dto.getPostId());
            return true;
        }
        throw new PostManagerException(ErrorType.USER_NOT_FOUND);
    }
}
