package org.arya.banking.user.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arya.banking.common.avro.UserCreateEvent;
import org.arya.banking.common.constants.RegistrationConstants;
import org.arya.banking.common.model.RegistrationProgress;
import org.arya.banking.common.model.SecurityDetails;
import org.arya.banking.common.model.User;
import org.arya.banking.common.utils.CommonUtils;
import org.arya.banking.user.config.kafka.UserCreateProducer;
import org.arya.banking.user.repository.RegistrationProgressRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.arya.banking.common.constants.RegistrationConstants.ADD_ADDRESS;
import static org.arya.banking.common.constants.RegistrationConstants.BASIC_DETAILS_ADDED;
import static org.arya.banking.common.constants.RegistrationConstants.SECURITY_CREDENTIALS_ADDED;

/**
 * Utility class for validating user registration steps and progress.
 * <p>
 * Provides methods to validate registration levels, security questions, and to update registration progress.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final RegistrationProgressRepository registrationProgressRepository;
    private final UserCreateProducer userCreateProducer;

    private static final List<Function<User, Object>> FIRST_LEVEL = List.of(
            User::getFirstName,
            User::getLastName,
            User::getPrimaryContactNumber,
            User::getEmailId);

    private static final List<Function<User, Object>> SECOND_LEVEL = List.of(User::getAddresss);

    private static final List<Function<SecurityDetails, Object>> THIRD_LEVEL = List.of(SecurityDetails::getSecurityQuestions);

    private static final List<List<Function<User, Object>>> LEVELS = List.of(FIRST_LEVEL, SECOND_LEVEL);

    /**
     * Validates the registration level of a user based on completed fields.
     *
     * @param user the user object to validate
     * @return the registration level (number of completed steps)
     */
    public int validateRegistrationLevel(User user) {

        int level = 0;
        for(List<Function<User, Object>> fields : LEVELS){
            boolean complete = getNoOfFields(fields, user) == fields.size();
            if(complete) ++level;
            else break;
        }
        return level;
    }

    /**
     * Returns the number of non-empty fields for a given user and field list.
     *
     * @param fields list of functions to extract fields from user
     * @param user the user object
     * @return number of non-empty fields
     */
    private int getNoOfFields(List<Function<User, Object>> fields, User user) {
        return Math.toIntExact(fields.stream()
                .map(field -> field.apply(user))
                .filter(CommonUtils::isNotEmpty).count()
        );
    }

    /**
     * Updates the registration step for a user based on their registration level.
     * <p>
     * Determines the registration progress and sends a registration event.
     * </p>
     * @param userId the unique identifier of the user
     * @param registrationLevel the current registration level
     */
    private void updateRegistrationStep(String userId, int registrationLevel) {
        log.info("User Registration Level: {}", registrationLevel);
        Optional<RegistrationProgress> registrationProgress = switch (registrationLevel) {
            case 1 -> validateAndGetRegistrationProgress(userId, BASIC_DETAILS_ADDED);
            case 2 -> validateAndGetRegistrationProgress(userId, ADD_ADDRESS);
            case 3 -> validateAndGetRegistrationProgress(userId, SECURITY_CREDENTIALS_ADDED);
            default -> Optional.empty();
        };
        String status = null;
        if(registrationProgress.isPresent()) {
            RegistrationProgress progress = registrationProgress.get();
            registrationProgressRepository.save(progress);
            status = progress.getSubStatus();
        }
        log.info("Send :{}, registration event", status);
        userCreateProducer.sendUserCreateEvent(getUserCreateEvent(userId, false, false, status));
    }

    /**
     * Validates and retrieves the registration progress for a user and registration constant.
     *
     * @param userId the unique identifier of the user
     * @param registrationConstants the registration constant
     * @return Optional containing the registration progress if not already present
     */
    private Optional<RegistrationProgress> validateAndGetRegistrationProgress(String userId, RegistrationConstants registrationConstants) {

        return registrationProgressRepository.findByUserIdAndSubStatus(userId, registrationConstants.getSubStatus())
                .isEmpty() ? Optional.of(generateRegistrationProgress(userId, registrationConstants))
                : Optional.empty();
    }

    /**
     * Creates a UserCreateEvent for user registration event publishing.
     *
     * @param userId the unique identifier of the user
     * @param isContactVerified whether the contact is verified
     * @param isEmailVerified whether the email is verified
     * @param status the registration status
     * @return UserCreateEvent object
     */
    public UserCreateEvent getUserCreateEvent(String userId, boolean isContactVerified, boolean isEmailVerified, String status) {
        return UserCreateEvent.newBuilder()
                .setUserId(userId).setStatus(status)
                .setIsContactVerified(isContactVerified)
                .setIsEmailVerified(isEmailVerified).build();
    }

    /**
     * Generates a RegistrationProgress object for a user and registration constant.
     *
     * @param userId the unique identifier of the user
     * @param registrationConstant the registration constant
     * @return RegistrationProgress object
     */
    public RegistrationProgress generateRegistrationProgress(String userId, RegistrationConstants registrationConstant) {
        return RegistrationProgress.builder()
                .userId(userId)
                .status(registrationConstant.getStatus())
                .subStatus(registrationConstant.getSubStatus())
                .lastStepCompleted(registrationConstant.getLastStepCompleted())
                .nextStep(registrationConstant.getNextStep()).build();
    }

    /**
     * Validates and invokes update of registration step for a user.
     *
     * @param user the user object
     * @param isSecurityDetailsUpdate flag indicating if security details are being updated
     * @param securityDetails the security details object
     */
    public void validateAndInvokeUpdateRegistrationStep(User user, boolean isSecurityDetailsUpdate, SecurityDetails securityDetails) {
        if(!validateFinalRegistrationStep(user.getUserId())) {
            int level = validateRegistrationLevel(user);
            if(isSecurityDetailsUpdate) {
                if(validateSecurityQuestionsSet(securityDetails)) {
                    level++;
                }
            }
            updateRegistrationStep(user.getUserId(), level);
        }
    }

    /**
     * Validates if the final registration step is completed for a user.
     *
     * @param userId the unique identifier of the user
     * @return true if registration is complete, false otherwise
     */
    private boolean validateFinalRegistrationStep(String userId) {
        return registrationProgressRepository.findByUserIdAndStatus(userId, "REGISTRATION_COMPLETE").isPresent();
    }

    /**
     * Validates if security questions are set in the SecurityDetails object.
     *
     * @param securityDetails the security details object
     * @return true if security questions are set, false otherwise
     */
    public boolean validateSecurityQuestionsSet(SecurityDetails securityDetails) {
        return THIRD_LEVEL.stream()
                .map(f -> f.apply(securityDetails))
                .allMatch(CommonUtils::isNotEmpty);
    }
}
