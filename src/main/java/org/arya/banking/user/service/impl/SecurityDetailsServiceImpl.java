package org.arya.banking.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.arya.banking.common.exception.SecurityDetailsNotFoundException;
import org.arya.banking.common.model.SecurityDetails;
import org.arya.banking.common.model.SecurityQuestions;
import org.arya.banking.common.utils.CommonUtils;
import org.arya.banking.user.dto.UpdateSecurityDetailsDto;
import org.arya.banking.user.dto.UserResponse;
import org.arya.banking.user.repository.SecurityDetailsRepository;
import org.arya.banking.user.service.SecurityDetailsService;
import org.arya.banking.user.service.UserService;
import org.arya.banking.user.util.UserValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.arya.banking.common.constants.ResponseCodes.USER_UPDATED_CODE;
import static org.arya.banking.common.exception.ExceptionCode.SECURITY_DETAILS_NOT_EXISTS_CODE;
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
    public UserResponse updateSecurityCredentials(String userId, UpdateSecurityDetailsDto updateSecurityDetailsDto) {

        SecurityDetails securityDetails = securityDetailsRepository.findByUserId(userId).orElseThrow(
                () -> new SecurityDetailsNotFoundException(NOT_FOUND_ERROR_CODE, SECURITY_DETAILS_NOT_EXISTS_CODE, "Security details not found"));

        List<SecurityQuestions> securityQuestions = new ArrayList<>();
        final Map<String, SecurityQuestions> securityQuestionsMap = isNotEmpty(securityDetails.getSecurityQuestions())
                ? CommonUtils.convertListIntoMap(securityDetails.getSecurityQuestions(), SecurityQuestions::getQuestion)
                : new HashMap<>();

        updateSecurityDetailsDto.securityQuestions().forEach(question -> {
            SecurityQuestions details = securityQuestionsMap.get(question.getQuestion());

            if(null != details) {
                details.setAnswer(question.getAnswer());
            } else {
                details = SecurityQuestions.builder()
                        .question(question.getQuestion())
                        .answer(question.getAnswer()).build();
            }
            securityQuestions.add(details);
        });
        securityDetails.setSecurityQuestions(securityQuestions);
        securityDetailsRepository.save(securityDetails);
        userValidator.validateAndInvokeUpdateRegistrationStep(userService.getUserById(userId), true, securityDetails);
        return new UserResponse(userId, "User updated successfully", USER_UPDATED_CODE);
    }
}
