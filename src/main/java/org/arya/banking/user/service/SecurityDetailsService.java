package org.arya.banking.user.service;

import org.arya.banking.user.dto.UpdateSecurityDetailsDto;

import java.util.Map;

public interface SecurityDetailsService {

    Map<String, String> updateSecurityCredentials(String userId, UpdateSecurityDetailsDto updateSecurityDetailsDto);
}
