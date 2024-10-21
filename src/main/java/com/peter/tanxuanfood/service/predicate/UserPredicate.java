package com.peter.tanxuanfood.service.predicate;

import com.peter.tanxuanfood.domain.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.StringUtils;

public class UserPredicate {
    private static final QUser qUser = QUser.user;

    private UserPredicate (){
        throw new AssertionError();
    }

    public static BooleanExpression containsName(String stringName) {
        return StringUtils.hasText(stringName) ? qUser.fullName.containsIgnoreCase(stringName) : null;
    }

    public static BooleanExpression containsEmail(String stringEmail) {
        return StringUtils.hasText(stringEmail) ? qUser.email.containsIgnoreCase(stringEmail) : null;
    }

    public static BooleanExpression containsPhone(String stringPhone) {
        return StringUtils.hasText(stringPhone) ? qUser.phone.containsIgnoreCase(stringPhone) : null;
    }

    public static BooleanExpression containsAddress(String stringAddress) {
        return StringUtils.hasText(stringAddress) ? qUser.address.containsIgnoreCase(stringAddress) : null;
    }

}
