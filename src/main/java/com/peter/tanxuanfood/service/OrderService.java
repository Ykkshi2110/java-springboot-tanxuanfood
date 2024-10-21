package com.peter.tanxuanfood.service;

import com.peter.tanxuanfood.domain.Meta;
import com.peter.tanxuanfood.domain.Order;
import com.peter.tanxuanfood.domain.OrderDetail;
import com.peter.tanxuanfood.domain.dto.OrderDTO;
import com.peter.tanxuanfood.domain.dto.ResultPaginationDTO;
import com.peter.tanxuanfood.exception.IdInValidException;
import com.peter.tanxuanfood.repository.OrderDetailRepository;
import com.peter.tanxuanfood.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;

    public OrderDTO handleUpdateOrder(Order order) {
        Order currentOrder = this.orderRepository.findById(order.getId()).orElseThrow(() -> new IdInValidException("User hasn't checked out any shopping cart"));
        currentOrder.setReceiverName(order.getReceiverName());
        currentOrder.setReceiverPhone(order.getReceiverPhone());
        currentOrder.setReceiverAddress(order.getReceiverAddress());
        currentOrder.setStatus(order.getStatus());
        this.orderRepository.save(currentOrder);
        return modelMapper.map(currentOrder, OrderDTO.class);
    }

    public void handleDeleteOrder(long id) {
        Order order = this.orderRepository.findById(id).orElseThrow(() -> new IdInValidException("Order does not exists"));
        Set<OrderDetail> orderDetails = order.getOrderDetails();
        for (OrderDetail orderDetail : orderDetails) {
            this.orderDetailRepository.deleteById(orderDetail.getId());
        }
        this.orderRepository.delete(order);
    }

    public ResultPaginationDTO handleFetchAllOrders(Pageable pageable){
        Page<Order> orderPage = this.orderRepository.findAll(pageable);
        Page<OrderDTO> orderDTOPage = orderPage.map(order -> modelMapper.map(order, OrderDTO.class));
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(orderDTOPage.getTotalPages());
        meta.setTotal(orderDTOPage.getTotalElements());
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setData(orderDTOPage.getContent());
        return resultPaginationDTO;
    }

    public OrderDTO handleFetchOrderById(long id) {
        Order order = this.orderRepository.findById(id).orElseThrow(() -> new IdInValidException("Order does not exists"));
        return modelMapper.map(order, OrderDTO.class);
    }

}
