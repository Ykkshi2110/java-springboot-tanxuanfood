package com.peter.tanxuanfood.service.predicate;

import com.peter.tanxuanfood.domain.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;

public class UserPredicate {
    private static final QUser qUser = QUser.user;

    private UserPredicate (){
        throw new AssertionError();
    }

    public static BooleanExpression containsName(String stringName) {
        return qUser.fullName.containsIgnoreCase(stringName);
    }


}
