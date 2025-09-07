package org.arya.banking.user.config;

import org.arya.banking.common.config.MongoConfig;
import org.springframework.context.annotation.Import;

@Import(MongoConfig.class)
public class UserServiceMongoConfig {

}
