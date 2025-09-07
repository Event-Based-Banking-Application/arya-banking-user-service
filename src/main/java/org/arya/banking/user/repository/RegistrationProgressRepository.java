package org.arya.banking.user.repository;

import org.arya.banking.common.model.RegistrationProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationProgressRepository extends MongoRepository<RegistrationProgress, String> {
}
