package org.arya.banking.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.arya.banking.common.dto.UserResponse;
import org.arya.banking.common.exception.SecurityDetailsNotFoundException;
import org.arya.banking.common.model.SecurityDetails;
import org.arya.banking.common.model.SecurityQuestions;
import org.arya.banking.common.utils.CommonUtils;
import org.arya.banking.user.dto.UpdateSecurityDetailsDto;
import org.arya.banking.user.dto.UserUpdateDto;
import org.arya.banking.user.repository.SecurityDetailsRepository;
import org.arya.banking.user.service.SecurityDetailsService;
import org.arya.banking.user.service.UserService;
import org.arya.banking.user.util.UserValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.arya.banking.common.constants.ResponseCodes.SECURITY_DETAILS_UPDATED_200;
import static org.arya.banking.common.constants.ResponseKeys.*;
import static org.arya.banking.common.exception.ExceptionCode.SECURITY_DETAILS_NOT_FOUND_404;
import static org.arya.banking.common.exception.ExceptionConstants.NOT_FOUND_ERROR_CODE;
import static org.arya.banking.common.utils.CommonUtils.isNotEmpty;

/**
 * Service implementation for managing user security details.
 * <p>
 * This class provides functionality to update security credentials for a user,
 * including handling security questions and answers.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SecurityDetailsServiceImpl implements SecurityDetailsService {

    private final SecurityDetailsRepository securityDetailsRepository;
    private final UserValidator userValidator;
    private final UserService userService;

    /**
     * Updates the security credentials for the specified user.
     * <p>
     * Finds the user's existing security details, updates the answers to security questions
     * as provided in the update DTO, and saves the changes.
     * </p>
     *
     * @param userId the ID of the user whose security credentials are to be updated
     * @param updateSecurityDetailsDto DTO containing updated security questions and answers
     * @return a {@link UserResponse} containing the result of the update operation, or null if not implemented
     * @throws SecurityDetailsNotFoundException if the user's security details are not found
     */
    @Override
    public Map<String, String> updateSecurityCredentials(String userId, UpdateSecurityDetailsDto updateSecurityDetailsDto) {

        SecurityDetails securityDetails = getSecurityDetails(userId);
        Map<String, String> response = getResponseMap(userId);
        response.put(RESPONSE_CODE, SECURITY_DETAILS_UPDATED_200);

        if (isNotEmpty(updateSecurityDetailsDto.securityQuestions())) {
            updateSecurityQuestions(updateSecurityDetailsDto, securityDetails);
            userValidator.validateAndInvokeUpdateRegistrationStep(userService.getUserById(userId), true, securityDetails);
            response.put(RESPONSE, "Security questions updated successfully");
        } else if (updateSecurityDetailsDto.loginFailed()) {
            securityDetails.setLoginFailedAttempts(securityDetails.getLoginFailedAttempts() + 1);
            validateAndLockAccount(securityDetails, response);
            response.put(DISABLE_USER, "true");
        }
        insertOrUpdateSecurityDetail(securityDetails);
        return response;
    }

    private void validateAndLockAccount(SecurityDetails securityDetails, Map<String, String> response) {

        if(securityDetails.getLoginFailedAttempts() >= 5) {
            response.put("response", "User account locked due to multiple failed login attempts");
            UserUpdateDto userUpdateDto = new UserUpdateDto(true, null, null);
            userService.updateUser(securityDetails.getUserId(), userUpdateDto);
        }
    }

    private void updateSecurityQuestions(UpdateSecurityDetailsDto updateSecurityDetailsDto, SecurityDetails securityDetails) {
        List<SecurityQuestions> securityQuestions = null != securityDetails.getSecurityQuestions() ? new ArrayList<>(securityDetails.getSecurityQuestions()) : new ArrayList<>();
        final Map<String, SecurityQuestions> securityQuestionsMap = isNotEmpty(securityDetails.getSecurityQuestions())
                ? CommonUtils.convertListIntoMap(securityDetails.getSecurityQuestions(), SecurityQuestions::getQuestion)
                : new HashMap<>();

        updateSecurityDetailsDto.securityQuestions().forEach(question -> {
            SecurityQuestions details = securityQuestionsMap.get(question.getQuestion());

            if(null != details) {
                securityQuestions.remove(details);
                details.setAnswer(question.getAnswer());
            } else {
                details = SecurityQuestions.builder()
                        .question(question.getQuestion())
                        .answer(question.getAnswer()).build();
            }
            securityQuestions.add(details);
        });
        securityDetails.setSecurityQuestions(securityQuestions);
    }

    private void insertOrUpdateSecurityDetail(SecurityDetails securityDetails) {
        securityDetailsRepository.save(securityDetails);
    }

    private SecurityDetails getSecurityDetails(String userId) {
        return securityDetailsRepository.findByUserId(userId).orElseThrow(
                () -> new SecurityDetailsNotFoundException(NOT_FOUND_ERROR_CODE, SECURITY_DETAILS_NOT_FOUND_404, "Security details not found"));
    }

    private Map<String, String> getResponseMap(String userId) {
        Map<String, String> response = new HashMap<>();
        response.put(USER_ID, userId);
        return response;
    }
}
