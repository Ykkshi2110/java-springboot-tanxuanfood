package com.peter.tanxuanfood.controller;

import com.peter.tanxuanfood.convert.annotation.ApiMessage;
import com.peter.tanxuanfood.domain.Product;
import com.peter.tanxuanfood.domain.dto.ResultPaginationDTO;
import com.peter.tanxuanfood.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/products/create")
    @ApiMessage("Create a product")
    public ResponseEntity<Product> handleCreateProduct(@Valid @RequestBody Product requestProduct) {
        Product product = this.productService.handleCreateProduct(requestProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/products/update")
    @ApiMessage("Update a product")
    public ResponseEntity<Product> handleUpdateProduct(@Valid @RequestBody Product requestProduct) {
        Product product = this.productService.handleUpdateProduct(requestProduct);
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @GetMapping("/products")
    @ApiMessage("Fetch all product")
    public ResponseEntity<ResultPaginationDTO> handleGetAllProduct(Pageable pageable) {
        ResultPaginationDTO resultPaginationDTO = this.productService.fetchAllProduct(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
    }

    @GetMapping("/products/{id}")
    @ApiMessage("Fetch a product")
    public ResponseEntity<Product> handleGetProduct(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.productService.fetchProductById(id));
    }

    @DeleteMapping("/products/delete/{id}")
    @ApiMessage("Delete a product")
    public ResponseEntity<Void> handleDeleteProduct(@PathVariable long id) {
        this.productService.handleDeleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }


}
