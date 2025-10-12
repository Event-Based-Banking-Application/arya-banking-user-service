package org.arya.banking.user;

import lombok.extern.slf4j.Slf4j;
import org.arya.banking.user.config.OAuth2FeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Slf4j
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@ComponentScan(basePackages = {"org.arya.banking.user", "org.arya.banking.common"})
@EnableMongoAuditing
@EnableDiscoveryClient
@EnableFeignClients(defaultConfiguration = OAuth2FeignConfig.class)
public class AryaBankingUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AryaBankingUserServiceApplication.class, args);
    }

}
