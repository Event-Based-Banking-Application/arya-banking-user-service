package org.arya.banking.user.repository;

import org.arya.banking.common.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    
}
