package org.arya.banking.user.util;

import org.arya.banking.common.model.User;
import org.arya.banking.common.utils.CommonUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class UserValidator {

    private static final List<Function<User, Object>> FIRST_LEVEL = List.of(
            User::getFirstName,
            User::getLastName,
            User::getPrimaryContactNumber,
            User::getEmailId);

    private static final List<Function<User, Object>> SECOND_LEVEL = List.of(User::getPrimaryAddress);

    private static final List<List<Function<User, Object>>> LEVELS = List.of(FIRST_LEVEL, SECOND_LEVEL);

    public List<Integer> validateRegistrationLevel(User user) {

        List<Integer> levelCompleted = new ArrayList<>();
        int level = 0;
        for(List<Function<User, Object>> fields : LEVELS){
            boolean complete = getNoOfFields(fields, user) == fields.size();
            if(complete) levelCompleted.add(level);
            else break;
        }

        return levelCompleted;
    }

    private int getNoOfFields(List<Function<User, Object>> fields, User user) {
        return Math.toIntExact(fields.stream()
                .map(field -> field.apply(user))
                .filter(CommonUtils::isNotEmpty).count()
        );
    }


}
