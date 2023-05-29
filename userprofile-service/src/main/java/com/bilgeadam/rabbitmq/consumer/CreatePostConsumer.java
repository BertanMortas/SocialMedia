package com.bilgeadam.rabbitmq.consumer;

import com.bilgeadam.mapper.IUserProfileMapper;
import com.bilgeadam.rabbitmq.model.AuthIdModel;
import com.bilgeadam.rabbitmq.model.GetUserprofileModel;
import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePostConsumer {
    private final UserProfileService userProfileService;

    @RabbitListener(queues = "createPost")
    public Object createPostFromHandleQueue(AuthIdModel model){
        UserProfile userProfile =userProfileService.findOptionalByAuthId(model.getAuthId()).get();
        return IUserProfileMapper.INSTANCE.userToModel(userProfile);
        //return GetUserprofileModel.builder().userId(userProfile.getId()).avatar(userProfile.getAvatar()).username(userProfile.getUsername()).build();
    }
}
