package com.bilgeadam.mapper;

import com.bilgeadam.dto.request.CreateFollowDto;
import com.bilgeadam.dto.request.NewCreateUserRegisterDto;
import com.bilgeadam.dto.request.UpdateEmailOrUsernameRequestDto;
import com.bilgeadam.dto.request.UserProfileUpdateRequestDto;
import com.bilgeadam.rabbitmq.model.GetUserprofileModel;
import com.bilgeadam.rabbitmq.model.RegisterElasticModel;
import com.bilgeadam.rabbitmq.model.RegisterModel;
import com.bilgeadam.repository.entity.Follow;
import com.bilgeadam.repository.entity.UserProfile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IUserProfileMapper {
    IUserProfileMapper INSTANCE = Mappers.getMapper(IUserProfileMapper.class);

    UserProfile fromDtoToUserNewRequest(NewCreateUserRegisterDto dto);
    UserProfile fromModelToUserNewRequest(RegisterModel createUserModel);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserProfile updateUserFromDto(UserProfileUpdateRequestDto dto, @MappingTarget UserProfile userProfile);
    UpdateEmailOrUsernameRequestDto toUpdateUsernameEmail(final UserProfile userProfile);
    UpdateEmailOrUsernameRequestDto toUpdateUsernameEmail(final UserProfileUpdateRequestDto dto);
    RegisterElasticModel fromUserToModelNewRequest(final UserProfile userProfile);
    Follow fromCreateFollowDtoToFollow(final String followId, final String userId);
    @Mapping(source = "id", target = "userId")
    GetUserprofileModel userToModel(final UserProfile userProfile);
}
