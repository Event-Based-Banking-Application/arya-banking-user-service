package org.arya.banking.user.repository;

import org.arya.banking.common.model.RegistrationProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationProgressRepository extends MongoRepository<RegistrationProgress, String> {

    Optional<RegistrationProgress> findByUserIdAndSubStatus(String userId, String subStatus);

    Optional<RegistrationProgress> findByUserIdAndStatus(String userId, String status);
}
