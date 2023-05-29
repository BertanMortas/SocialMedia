package com.bilgeadam.mapper;

import com.bilgeadam.dto.response.UserProfileResponseDto;
import com.bilgeadam.repository.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ILikeMapper {
    ILikeMapper INSTANCE = Mappers.getMapper(ILikeMapper.class);
    @Mapping(source = "id", target = "userId")
    Like toLikeFromDto(final UserProfileResponseDto dto);
}
