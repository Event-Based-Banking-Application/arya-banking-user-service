package org.arya.banking.user.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.common.dto.KeyCloakResponse;
import org.arya.banking.common.dto.UserResponse;
import org.arya.banking.common.exception.UserAlreadyExistsException;
import org.arya.banking.common.exception.UserNotFoundException;
import org.arya.banking.common.model.*;
import org.arya.banking.user.config.kafka.UserCreateProducer;
import org.arya.banking.user.dto.RegisterDto;
import org.arya.banking.user.dto.UpdateAddressDto;
import org.arya.banking.user.dto.UpdateContactDto;
import org.arya.banking.user.dto.UserUpdateDto;
import org.arya.banking.user.external.KeyCloakService;
import org.arya.banking.user.mapper.UserMapper;
import org.arya.banking.user.repository.RegistrationProgressRepository;
import org.arya.banking.user.repository.SecurityDetailsRepository;
import org.arya.banking.user.repository.UserRepository;
import org.arya.banking.user.service.UserService;
import org.arya.banking.user.util.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.arya.banking.common.constants.RegistrationConstants.BASIC_DETAILS_ADDED;
import static org.arya.banking.common.constants.ResponseCodes.USER_CREATED_201;
import static org.arya.banking.common.constants.ResponseCodes.USER_UPDATED_200;
import static org.arya.banking.common.exception.ExceptionCode.USER_ALREADY_EXISTS_409;
import static org.arya.banking.common.exception.ExceptionCode.USER_NOT_FOUND_404;
import static org.arya.banking.common.exception.ExceptionConstants.CONFLICT_ERROR_CODE;
import static org.arya.banking.common.exception.ExceptionConstants.NOT_FOUND_ERROR_CODE;
import static org.arya.banking.common.utils.CommonUtils.generateSHA256hash;

/**
 * Implementation of the UserService interface for managing user operations.
 * <p>
 * This service provides methods for user registration, updating user details, retrieving user information,
 * and handling registration progress and security details. It interacts with repositories, external services,
 * and event producers to manage user lifecycle and registration events.
 * </p>
 */
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
    private final UserValidator userValidator;

    /**
     * Registers a new user in the system.
     * <p>
     * Validates if the user already exists, creates a new user entity, saves it, creates a KeyCloak user,
     * initializes registration progress and security details, and sends a user creation event.
     * </p>
     * @param registerDto DTO containing user registration details
     * @return UserResponse containing the user ID and registration status
     * @throws UserAlreadyExistsException if a user with the same email or contact number already exists
     */
    @Override
    public UserResponse register(RegisterDto registerDto) {
        
        userRepository.findByEmailIdOrPrimaryContactNumber(registerDto.emailId(),
                        registerDto.primaryContactNumber()).ifPresent(user -> { throw new UserAlreadyExistsException(CONFLICT_ERROR_CODE, USER_ALREADY_EXISTS_409, "User already exists"); });

        User user = userMapper.toEntity(registerDto);
        user.setUserId(generateUserId(registerDto.firstName(), registerDto.lastName()));
        user.setContactNumbers(List.of(ContactNumber.builder()
                .contactNumber(registerDto.primaryContactNumber())
                .type(ContactNumberType.PRIMARY)
                .isVerified(false).build()));

        user.setStatus(UserStatus.ACTIVE.name());
        insertOrUpdateUser(user);
        KeyCloakUser keyCloakUser = KeyCloakUser.builder().username(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .password(registerDto.password()).build();

        ResponseEntity<KeyCloakResponse> response = keyCloakService.createKeyCloakUser(keyCloakUser);

        log.debug("Response from keycloak: {}", response);
        registrationProgressRepository.save(userValidator.generateRegistrationProgress(user.getUserId(), BASIC_DETAILS_ADDED));

        SecurityDetails securityDetails = SecurityDetails.builder()
                .userId(user.getUserId())
                .isContactNumberVerified(false)
                .isEmailVerified(false)
                .twoFactorEnabled(false)
                .loginFailedAttempts(0).build();
        securityDetailsRepository.save(securityDetails);

        userCreateProducer.sendUserCreateEvent(userValidator.getUserCreateEvent(user.getUserId(), false, false, BASIC_DETAILS_ADDED.getSubStatus()));
        return new UserResponse(user.getUserId(), "User Registered Successfully", USER_CREATED_201);
    }

    /**
     * Retrieves a user by their unique user ID.
     *
     * @param userId the unique identifier of the user
     * @return the User entity
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    public User getUserById(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException(NOT_FOUND_ERROR_CODE, USER_NOT_FOUND_404, "User not present"));
    }

    /**
     * Generates a unique user ID based on first name, last name, and current timestamp.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @return a unique user ID string
     */
    private String generateUserId(String firstName, String lastName) {

        StringBuilder valueToHash = new StringBuilder(firstName);
        valueToHash.append(lastName).append(System.currentTimeMillis());
        return "ARYA"+generateSHA256hash(valueToHash.toString()).substring(0, 6).toUpperCase();
    }

    /**
     * Updates user details such as contact number and address.
     * <p>
     * Validates and updates contact and address information, updates registration step, and saves the user.
     * </p>
     * @param userId the unique identifier of the user
     * @param userUpdateDto DTO containing updated user details
     * @return UserResponse containing the user ID and update status
     */
    @Override
    public UserResponse updateUser(String userId, UserUpdateDto userUpdateDto) {

        User user = getUserById(userId);
        if (!userUpdateDto.isLockUser()) {
            if (null != userUpdateDto.updateContactDto()) {
                updateContactNumber(user, userUpdateDto.updateContactDto());
            }
            if (null != userUpdateDto.updateAddressDto()) {
                updateAddress(user, userUpdateDto.updateAddressDto());
            }
            userValidator.validateAndInvokeUpdateRegistrationStep(user, false, null);
        } else {
            user.setStatus(UserStatus.BLOCKED.name());
            userValidator.sendUserEvent(user.getStatus(), userId);
        }
        insertOrUpdateUser(user);
        return new UserResponse(user.getUserId(), "User updated successfully", USER_UPDATED_200);
    }

    private void insertOrUpdateUser(User user) {
        userRepository.save(user);
    }

    /**
     * Updates the address of a user.
     * <p>
     * Removes any existing address of the same type and adds the new address.
     * </p>
     * @param user the User entity to update
     * @param updateAddressDto DTO containing the new address information
     */
    private void updateAddress(User user, @Valid UpdateAddressDto updateAddressDto) {

        if(null == user.getAddresss()) {
            user.setAddresss(new ArrayList<>());
        }
        user.getAddresss().removeIf(address -> address.getAddressType().equals(updateAddressDto.address().getAddressType()));
        user.getAddresss().add(updateAddressDto.address());
    }

    /**
     * Updates the contact number of a user.
     * <p>
     * Handles primary and other contact numbers, updating types and verification status as needed.
     * </p>
     * @param user the User entity to update
     * @param updateContactDto DTO containing the new contact information
     */
    private void updateContactNumber(User user, @Valid UpdateContactDto updateContactDto) {

        Optional<ContactNumber> contactNumber = getExistingContactNumber(user, updateContactDto.contactNumber());

        if (updateContactDto.isPrimary()) {

            user.getContactNumbers().stream().filter(contact -> contact.getType().equals(ContactNumberType.PRIMARY))
                    .findFirst()
                    .ifPresent(contact -> contact.setType(ContactNumberType.OTHERS));
            user.setPrimaryContactNumber(updateContactDto.contactNumber());
            if(contactNumber.isPresent() && !ContactNumberType.PRIMARY.equals(contactNumber.get().getType())) {
                contactNumber.ifPresent(number  -> number.setType(ContactNumberType.PRIMARY));
            } else {
                addContactNumber(user, updateContactDto.contactNumber(), ContactNumberType.PRIMARY);
            }
        } else {
            addContactNumber(user, updateContactDto.contactNumber(), ContactNumberType.OTHERS);
        }
    }

    /**
     * Retrieves an existing contact number from a user.
     *
     * @param user the User entity
     * @param contactNumber the contact number to search for
     * @return Optional containing the ContactNumber if found
     */
    private static Optional<ContactNumber> getExistingContactNumber(User user, String contactNumber) {
        return user.getContactNumbers().stream()
                .filter(contact -> contact.getContactNumber().equals(contactNumber))
                .findFirst();
    }

    /**
     * Adds a contact number to a user.
     *
     * @param user the User entity
     * @param contactNumber the contact number to add
     * @param primary the type of contact number (PRIMARY or OTHERS)
     */
    private static void addContactNumber(User user, String contactNumber, ContactNumberType primary) {
        user.getContactNumbers().add(
                ContactNumber.builder()
                        .contactNumber(contactNumber)
                        .isVerified(false)
                        .type(primary).build()
        );
    }
    
}
