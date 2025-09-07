package org.arya.banking.user.mapper;

import java.util.List;

import org.arya.banking.common.mapper.BaseMapper;
import org.arya.banking.common.model.User;
import org.arya.banking.user.dto.RegisterDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, RegisterDto> {

    User toEntity(RegisterDto registerDto);

    RegisterDto toDto(User user);

    List<User> toEntityList(List<RegisterDto> registerDtos);
    
    List<RegisterDto> toDtoList(List<User> users);
    
}
