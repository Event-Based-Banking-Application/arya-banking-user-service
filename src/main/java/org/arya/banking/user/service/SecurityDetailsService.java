package org.arya.banking.user.service;

import org.arya.banking.user.dto.UpdateSecurityDetailsDto;
import org.arya.banking.user.dto.UserResponse;

public interface SecurityDetailsService {

    UserResponse updateSecurityCredentials(String userId, UpdateSecurityDetailsDto updateSecurityDetailsDto);
}
