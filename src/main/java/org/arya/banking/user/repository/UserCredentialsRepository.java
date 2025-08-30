package org.arya.banking.user.repository;

import org.arya.banking.common.model.UserCredentials;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialsRepository extends MongoRepository<UserCredentials, String> {
}
