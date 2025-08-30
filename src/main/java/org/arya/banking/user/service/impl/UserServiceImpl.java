package org.arya.banking.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.common.exception.UserAlreadyExistsException;
import org.arya.banking.common.model.ContactNumber;
import org.arya.banking.common.model.ContactNumberType;
import org.arya.banking.common.model.KeyCloakUser;
import org.arya.banking.common.model.User;
import org.arya.banking.common.model.UserCredentials;
import org.arya.banking.user.dto.RegisterDto;
import org.arya.banking.user.dto.UserResponse;
import org.arya.banking.user.external.KeyCloakService;
import org.arya.banking.user.mapper.UserMapper;
import org.arya.banking.user.repository.UserCredentialsRepository;
import org.arya.banking.user.repository.UserRepository;
import org.arya.banking.user.service.UserService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.arya.banking.common.exception.ExceptionConstants.CONFLICT_ERROR_CODE;
import static org.arya.banking.common.utils.CommonUtils.generateSHA256hash;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCredentialsRepository UserCredentialsRepository;
    private final UserMapper userMapper;
    private final KeyCloakService keyCloakService;

    @Override
    public UserResponse register(RegisterDto registerDto) {
        
        userRepository.findByEmailIdOrPrimaryContactNumber(registerDto.emailId(),
                        registerDto.primaryContactNumber()).ifPresent(user -> { throw new UserAlreadyExistsException(CONFLICT_ERROR_CODE, null, "User already exists"); });

        User user = userMapper.toEntity(registerDto);
        user.setUserId(generateUserId(registerDto.firstName(), registerDto.lastName()));
        user.setContactNumbers(List.of(ContactNumber.builder()
                .contactNumber(registerDto.primaryContactNumber())
                .type(ContactNumberType.PRIMARY)
                .isVerified(false).build()));

        UserCredentials userCredentials = UserCredentials.builder()
                        .passwordHash(Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8().encode(registerDto.password()))
                .userId(user.getUserId()).build();
        UserCredentialsRepository.save(userCredentials);

        userRepository.save(user);
        KeyCloakUser keyCloakUser = KeyCloakUser.builder().username(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .password(registerDto.password()).build();
        log.info("Processing key cloak user: {}", keyCloakUser);
        keyCloakService.createKeyCloakUser(keyCloakUser);
        
        return new UserResponse(user.getUserId(), "User Registered Successfully", "AO1");
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new UserAlreadyExistsException(CONFLICT_ERROR_CODE, "User not present", ""));
    }

    private String generateUserId(String firstName, String lastName) {

        StringBuilder valueToHash = new StringBuilder(firstName);
        valueToHash.append(lastName).append(System.currentTimeMillis());
        return "ARYA"+generateSHA256hash(valueToHash.toString()).substring(0, 6).toUpperCase();
    }

    
}
