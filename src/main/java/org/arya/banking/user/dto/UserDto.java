package org.arya.banking.user.dto;


import org.arya.banking.common.model.Address;

public record UserDto(String firstName, String lastName, String emailId, String primaryContactNumber,
                      Address primaryAddress, String status, String role) {

    
}
