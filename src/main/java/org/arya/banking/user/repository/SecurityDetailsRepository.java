package org.arya.banking.user.repository;

import org.arya.banking.common.model.SecurityDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityDetailsRepository extends MongoRepository<SecurityDetails, String> {

    Optional<SecurityDetails> findByUserId(String userId);
}
