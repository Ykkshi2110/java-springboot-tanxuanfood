package com.peter.tanxuanfood.controller.admin;

import com.peter.tanxuanfood.convert.annotation.ApiMessage;
import com.peter.tanxuanfood.domain.Order;
import com.peter.tanxuanfood.domain.dto.OrderDTO;
import com.peter.tanxuanfood.domain.dto.ResultPaginationDTO;
import com.peter.tanxuanfood.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/orders")
    @ApiMessage("Fetch all order")
    public ResponseEntity<ResultPaginationDTO> handleFetchAllOrder(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.orderService.handleFetchAllOrders(pageable));
    }

    @GetMapping("/orders/{id}")
    @ApiMessage("Fetch order by id")
    public ResponseEntity<OrderDTO> handleFetchOrderById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.orderService.handleFetchOrderById(id));
    }

    @PutMapping("/orders/update")
    @ApiMessage("Update a order")
    public ResponseEntity<OrderDTO> handleUpdateOrder(@RequestBody Order order) {
        return ResponseEntity.status(HttpStatus.OK).body(this.orderService.handleUpdateOrder(order));
    }

    @DeleteMapping("/orders/delete/{id}")
    @ApiMessage("Delete a order")
    public ResponseEntity<Void> handleDeleteOrder(@PathVariable Long id) {
        this.orderService.handleDeleteOrder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
