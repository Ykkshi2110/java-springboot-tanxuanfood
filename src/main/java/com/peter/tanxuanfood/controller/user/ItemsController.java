package com.peter.tanxuanfood.controller.user;

import com.peter.tanxuanfood.convert.annotation.ApiMessage;
import com.peter.tanxuanfood.convert.util.SecurityUtil;
import com.peter.tanxuanfood.domain.dto.CheckOutResponse;
import com.peter.tanxuanfood.domain.dto.ResAddProductDTO;
import com.peter.tanxuanfood.domain.request.AddProductRequest;
import com.peter.tanxuanfood.domain.request.OrderInformationRequest;
import com.peter.tanxuanfood.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class ItemsController {
    private final ProductService productService;

    @PostMapping("/add-product-to-cart")
    @ApiMessage("Added product successfully")
    public ResponseEntity<ResAddProductDTO> addProductToCart(@Valid @RequestBody AddProductRequest addProductRequest){
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        long productId = addProductRequest.getProductId();
        long quantity = addProductRequest.getQuantity();
        ResAddProductDTO resAddProductDTO = this.productService.handleAddProductToCart(email, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(resAddProductDTO);
    }

    @DeleteMapping("/delete-product-in-cart/{id}")
    @ApiMessage("Delete product in cart")
    public ResponseEntity<String> handleDeleteProductInCart(@PathVariable long id){
        this.productService.handleDeleteProductInCart(id);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted in cart successfully");
    }

    @GetMapping("/pre-checkout")
    @ApiMessage("Pre-CheckOut")
    public ResponseEntity<CheckOutResponse> handlePreCheckout(){
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        return ResponseEntity.status(HttpStatus.OK).body(this.productService.preCheckOut(email));
    }

    @PostMapping("/checkout")
    @ApiMessage("Checkout")
    public ResponseEntity<CheckOutResponse> handleCheckout(@Valid @RequestBody OrderInformationRequest orderInformationRequest){
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.handleCheckOut(email, orderInformationRequest));

    }
    
}
