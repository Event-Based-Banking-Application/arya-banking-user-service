package org.arya.banking.user.mapper;

import java.util.List;

import org.arya.banking.common.mapper.BaseMapper;
import org.arya.banking.common.model.User;
import org.arya.banking.user.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDto> {

    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    List<User> toEntityList(List<UserDto> userDtos);
    
    List<UserDto> toDtoList(List<User> users);
    
}
