package com.peter.tanxuanfood.repository;

import com.peter.tanxuanfood.domain.Cart;
import com.peter.tanxuanfood.domain.CartDetail;
import com.peter.tanxuanfood.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long>, QuerydslPredicateExecutor<CartDetail> {
    CartDetail findByCartAndProduct(Cart cart, Product product);
}
