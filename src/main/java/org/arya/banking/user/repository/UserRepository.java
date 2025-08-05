package org.arya.banking.user.repository;

import java.util.Optional;

import org.arya.banking.common.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmailIdOrPrimaryContactNumber(String emailId, String contactNumber);
    
}
