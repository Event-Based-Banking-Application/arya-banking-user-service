package org.arya.banking.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.arya.banking.common.exception.UserAlreadyExistsException;
import org.arya.banking.common.model.ContactNumber;
import org.arya.banking.common.model.ContactNumberType;
import org.arya.banking.common.model.KeyCloakUser;
import org.arya.banking.common.model.User;
import org.arya.banking.user.dto.UserDto;
import org.arya.banking.user.dto.UserResponse;
import org.arya.banking.user.external.KeyCloakService;
import org.arya.banking.user.mapper.UserMapper;
import org.arya.banking.user.repository.UserRepository;
import org.arya.banking.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import static org.arya.banking.common.exception.ExceptionConstants.CONFLICT_ERROR_CODE;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeyCloakService keyCloakService;

    @Override
    public UserResponse register(UserDto userDto) {
        
        userRepository.findByEmailIdOrPrimaryContactNumber(userDto.emailId(), 
                        userDto.primaryContactNumber()).ifPresent(user -> { throw new UserAlreadyExistsException(CONFLICT_ERROR_CODE, null, "User already exists"); });

        User user = userMapper.toEntity(userDto);
        user.setUserId(generateUserId(userDto.firstName(), userDto.lastName()));
        user.setAddresss(List.of(userDto.primaryAddress()));
        user.setContactNumbers(List.of(ContactNumber.builder()
                        .contactNumber(userDto.primaryContactNumber())
                        .type(ContactNumberType.PRIMARY)
                        .isVerified(false).build()));

        userRepository.save(user);
        KeyCloakUser keyCloakUser = new KeyCloakUser();
        keyCloakUser.setUsername(user.getUserId());
        keyCloakUser.setFirstName(user.getFirstName());
        keyCloakUser.setLastName(user.getLastName());
        keyCloakUser.setEmailId(user.getEmailId());
        keyCloakUser.setPassword("dummy");
        log.info("Processing key cloak user: {}", keyCloakUser);
        keyCloakService.createKeyCloakUser(keyCloakUser);
        
        return new UserResponse(user.getUserId(), "User Registered Successfully", "AO1");
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new UserAlreadyExistsException(CONFLICT_ERROR_CODE, "User not present", ""));
    }

    private String generateUserId(String firstName, String lastName) {

        return "ARYA"+DigestUtils.sha256Hex(firstName+lastName+System.currentTimeMillis()).substring(0, 6).toUpperCase();
    }

    
}
