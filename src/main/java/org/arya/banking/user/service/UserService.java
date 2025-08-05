package org.arya.banking.user.service;

import org.arya.banking.common.model.User;
import org.arya.banking.user.dto.UserDto;
import org.arya.banking.user.dto.UserResponse;

public interface UserService {
    
    UserResponse register(UserDto userDto);

    User getUserById(String userId);
    
}
