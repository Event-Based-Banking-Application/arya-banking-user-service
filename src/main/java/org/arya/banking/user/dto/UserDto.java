package org.arya.banking.user.dto;

import java.util.List;

import org.arya.banking.common.model.Address;
import org.arya.banking.common.model.ContactNumber;

public record UserDto(String userId, String firstName, String lastName, String emailId,
                      List<ContactNumber> contactNumbers, List<Address> addresses,
                      Address primaryAddress, String status, String role) {

    
}
