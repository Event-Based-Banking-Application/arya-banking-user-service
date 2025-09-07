package org.arya.banking.user.external;

import org.arya.banking.common.config.FeignConfiguration;
import org.arya.banking.common.dto.KeyCloakResponse;
import org.arya.banking.common.model.KeyCloakUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ARYA-BANKING-AUTH-SERVICE", configuration = FeignConfiguration.class)
public interface KeyCloakService {

    @PostMapping("/api/auth/register/users")
    ResponseEntity<KeyCloakResponse> createKeyCloakUser(@RequestBody KeyCloakUser keyCloakUser);
}
