package com.peter.tanxuanfood.service;

import com.peter.tanxuanfood.domain.*;
import com.peter.tanxuanfood.domain.dto.ProductDTO;
import com.peter.tanxuanfood.domain.dto.ResAddProductDTO;
import com.peter.tanxuanfood.domain.dto.ResultPaginationDTO;
import com.peter.tanxuanfood.exception.IdInValidException;
import com.peter.tanxuanfood.repository.CartDetailRepository;
import com.peter.tanxuanfood.repository.CartRepository;
import com.peter.tanxuanfood.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    private static final String PRODUCT_EXISTS = "Product does not exist";
    private final ModelMapper modelMapper;

    public ProductDTO mapToDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    public ResultPaginationDTO fetchAllProduct(Pageable pageable) {
        Page<Product> pageProducts = productRepository.findAll(pageable);
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageProducts.getTotalPages());
        meta.setTotal(pageProducts.getTotalElements());
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setData(pageProducts.getContent());
        return resultPaginationDTO;
    }

    public Product fetchProductById(long id) {
        return this.productRepository
                .findById(id)
                .orElseThrow(() -> new IdInValidException(PRODUCT_EXISTS));
    }

    public Product handleCreateProduct(@Valid Product requestProduct){
        if(this.productRepository.existsByName(requestProduct.getName())) throw new IdInValidException("Product already exists");
        Product product = new Product();
        product.setName(requestProduct.getName());
        product.setPrice(requestProduct.getPrice());
        product.setDescription(requestProduct.getDescription());
        product.setStockQuantity(requestProduct.getStockQuantity());
        product.setIsAvailable(requestProduct.getIsAvailable());
        return this.productRepository.save(product);
    }

    public Product handleUpdateProduct(Product requestProduct){
        Product product = this.productRepository.findById(requestProduct.getId()).orElseThrow(() -> new IdInValidException(PRODUCT_EXISTS));
        product.setName(requestProduct.getName());
        product.setPrice(requestProduct.getPrice());
        product.setDescription(requestProduct.getDescription());
        product.setStockQuantity(requestProduct.getStockQuantity());
        product.setIsAvailable(requestProduct.getIsAvailable());
        return this.productRepository.save(product);
    }

    public void handleDeleteProduct(long id){
        Product product = this.productRepository.findById(id).orElseThrow(() -> new IdInValidException(PRODUCT_EXISTS));
        this.productRepository.delete(product);
    }

    public List<ProductDTO> fetchAllProductInCart(Cart cart){
        Set<CartDetail> cartDetails = cart.getCartDetails();
        List<ProductDTO> productDTOList = new ArrayList<>();
        if(cartDetails != null && !cartDetails.isEmpty()){
             productDTOList = cartDetails.stream().map(cartDetail -> modelMapper.map(cartDetail.getProduct(), ProductDTO.class)).toList();
        }
        return productDTOList;
    }

    public ResAddProductDTO handleAddProductToCart(String email, long productId, long productQuantity){
        User user = this.userService.handleGetUserByUserName(email);
        ResAddProductDTO resAddProductDTO = new ResAddProductDTO();

        // kiểm tra xem nó có Cart chưa
        Cart currentCart = this.cartRepository.findByUser(user);
        if(currentCart == null){
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setSum(0);
            // lưu Cart
            currentCart = this.cartRepository.save(cart);
        }

        resAddProductDTO.setCartId(currentCart.getId());

        CartDetail cartDetail = new CartDetail();

        // Kiểm tra thử sp đó đã có trong giỏ hàng hay chưa
        Product product = this.fetchProductById(productId);
        CartDetail oldCartDetail = this.cartDetailRepository.findByCartAndProduct(currentCart, product);
        ResAddProductDTO.ResCartDTO respCartDTO = new ResAddProductDTO.ResCartDTO();

        // Nếu chưa có trong giỏ hàng
        if(oldCartDetail == null){
            cartDetail.setCart(currentCart);
            cartDetail.setProduct(product);
            cartDetail.setQuantity(productQuantity);
            cartDetail.setPrice(product.getPrice());

            // update Cart
            long s = currentCart.getSum() + 1;
            currentCart.setSum(s);
            this.cartDetailRepository.save(cartDetail);
        } else {
            oldCartDetail.setQuantity(oldCartDetail.getQuantity()+productQuantity);
            this.cartDetailRepository.save(oldCartDetail);
        }
        respCartDTO.setSum(currentCart.getSum());

        // Load lại Cart
        currentCart = this.cartRepository.findById(currentCart.getId()).orElse(currentCart);
        respCartDTO.setItems(this.fetchAllProductInCart(currentCart));
        resAddProductDTO.setCart(respCartDTO);
        return  resAddProductDTO;
    }

    public void handleDeleteProductInCart(long id){
        CartDetail cartDetail = this.cartDetailRepository.findById(id).orElseThrow(() -> new IdInValidException("Product is not in Cart"));
        Cart cart = cartDetail.getCart();
        this.cartDetailRepository.deleteById(id);
        long s = cart.getSum();
        // Nếu trong giỏ hàng có nhiều sp
        if(s > 1){
            s -= 1;
            cart.setSum(s);
            this.cartRepository.save(cart);
        } else { // Nếu giỏ hàng chỉ có 1 sản phẩm
            this.cartRepository.delete(cart);
        }
    }
}
