package com.peter.tanxuanfood.service.predicate;

import com.peter.tanxuanfood.domain.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.StringUtils;

public class ProductPredicate {
    private static final QProduct qProduct = QProduct.product;

    private ProductPredicate() {
        throw new AssertionError();
    }

    public static BooleanExpression containsName(String name){
        return StringUtils.hasText(name) ? qProduct.name.containsIgnoreCase(name) : null;
    }

    public static BooleanExpression comparePrice(Double minPrice, Double maxPrice){
        if(minPrice == null && maxPrice == null){
            return null;
        } else if(minPrice == null){
            return qProduct.price.loe(maxPrice);
        } else if(maxPrice == null){
            return qProduct.price.goe(minPrice);
        } else {
            return qProduct.price.between(minPrice, maxPrice);
        }
    }

    public static BooleanExpression availableProduct(boolean available){
        if(available){
            return qProduct.isAvailable.isTrue();
        } else {
            return qProduct.isAvailable.isFalse();
        }
    }
}
