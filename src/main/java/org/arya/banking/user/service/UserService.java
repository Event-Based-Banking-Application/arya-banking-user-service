package org.arya.banking.user.service;

import org.arya.banking.common.model.User;
import org.arya.banking.user.dto.RegisterDto;
import org.arya.banking.user.dto.UserResponse;

public interface UserService {
    
    UserResponse register(RegisterDto registerDto);

    User getUserById(String userId);
    
}
