package com.peter.tanxuanfood.repository;

import com.peter.tanxuanfood.domain.Cart;
import com.peter.tanxuanfood.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, QuerydslPredicateExecutor<Cart> {
    Cart findByUser (User user);
}
