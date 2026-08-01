package org.arya.banking.user.repository;

import org.arya.banking.outbox.repository.OutboxEventRepository;
import org.arya.banking.user.outbox.UserOutboxEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOutboxEventRepository extends OutboxEventRepository<UserOutboxEvent> {
}
