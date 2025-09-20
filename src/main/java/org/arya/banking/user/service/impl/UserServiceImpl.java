package org.arya.banking.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.common.avro.UserCreateEvent;
import org.arya.banking.common.dto.KeyCloakResponse;
import org.arya.banking.common.exception.UserAlreadyExistsException;
import org.arya.banking.common.model.*;
import org.arya.banking.user.config.kafka.UserCreateProducer;
import org.arya.banking.user.dto.RegisterDto;
import org.arya.banking.user.dto.UserResponse;
import org.arya.banking.user.external.KeyCloakService;
import org.arya.banking.user.mapper.UserMapper;
import org.arya.banking.user.repository.RegistrationProgressRepository;
import org.arya.banking.user.repository.SecurityDetailsRepository;
import org.arya.banking.user.repository.UserRepository;
import org.arya.banking.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.arya.banking.common.ResponseCodes.USER_CREATED_CODE;
import static org.arya.banking.common.constants.RegistrationConstants.BASIC_DETAILS_ADDED;
import static org.arya.banking.common.exception.ExceptionCode.USER_EXISTS_CODE;
import static org.arya.banking.common.exception.ExceptionConstants.CONFLICT_ERROR_CODE;
import static org.arya.banking.common.utils.CommonUtils.generateSHA256hash;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RegistrationProgressRepository registrationProgressRepository;
    private final SecurityDetailsRepository securityDetailsRepository;
    private final UserMapper userMapper;
    private final KeyCloakService keyCloakService;
    private final UserCreateProducer userCreateProducer;

    @Override
    public UserResponse register(RegisterDto registerDto) {
        
        userRepository.findByEmailIdOrPrimaryContactNumber(registerDto.emailId(),
                        registerDto.primaryContactNumber()).ifPresent(user -> { throw new UserAlreadyExistsException(CONFLICT_ERROR_CODE, USER_EXISTS_CODE, "User already exists"); });

        User user = userMapper.toEntity(registerDto);
        user.setUserId(generateUserId(registerDto.firstName(), registerDto.lastName()));
        user.setContactNumbers(List.of(ContactNumber.builder()
                .contactNumber(registerDto.primaryContactNumber())
                .type(ContactNumberType.PRIMARY)
                .isVerified(false).build()));

        user.setStatus(UserStatus.ACTIVE.name());
        userRepository.save(user);
        KeyCloakUser keyCloakUser = KeyCloakUser.builder().username(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .password(registerDto.password()).build();

        ResponseEntity<KeyCloakResponse> response = keyCloakService.createKeyCloakUser(keyCloakUser);

        log.debug("Response from keycloak: {}", response);
        registrationProgressRepository.save(RegistrationProgress.builder()
                .userId(user.getUserId())
                .status(BASIC_DETAILS_ADDED.getStatus())
                .subStatus(BASIC_DETAILS_ADDED.getSubStatus())
                .lastStepCompleted(BASIC_DETAILS_ADDED.getLastStepCompleted())
                .nextStep(BASIC_DETAILS_ADDED.getNextStep()).build());

        securityDetailsRepository.save(SecurityDetails.builder()
                .userId(user.getUserId())
                .isContactNumberVerified(false)
                .isEmailVerified(false)
                .twoFactorEnabled(false)
                .loginFailedAttempts(0).build());

        userCreateProducer.sendUserCreateEvent(UserCreateEvent.newBuilder()
                .setUserId(user.getUserId()).setStatus(user.getStatus()).build());
        return new UserResponse(user.getUserId(), "User Registered Successfully", USER_CREATED_CODE);
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
