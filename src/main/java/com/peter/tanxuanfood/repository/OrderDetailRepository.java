package com.peter.tanxuanfood.repository;

import com.peter.tanxuanfood.domain.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>, QuerydslPredicateExecutor<OrderDetail> {
}
