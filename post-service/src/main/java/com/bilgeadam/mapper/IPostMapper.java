package com.bilgeadam.mapper;

import com.bilgeadam.dto.request.CreateNewPostRequestDto;
import com.bilgeadam.dto.request.UpdatePostRequestDto;
import com.bilgeadam.rabbitmq.model.GetUserprofileModel;
import com.bilgeadam.repository.entity.Post;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface IPostMapper {
    IPostMapper INSTANCE = Mappers.getMapper(IPostMapper.class);
    Post toPost(final CreateNewPostRequestDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post toPost(final UpdatePostRequestDto dto);
    @Mapping(source = "userId", target = "userId")
    Post toPostFromModel(final GetUserprofileModel model);
}
