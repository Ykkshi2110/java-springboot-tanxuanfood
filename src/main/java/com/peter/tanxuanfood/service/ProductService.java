package com.peter.tanxuanfood.service;

import com.peter.tanxuanfood.domain.*;
import com.peter.tanxuanfood.domain.dto.*;
import com.peter.tanxuanfood.domain.request.OrderInformationRequest;
import com.peter.tanxuanfood.exception.IdInValidException;
import com.peter.tanxuanfood.exception.Unauthenticated;
import com.peter.tanxuanfood.repository.*;
import com.peter.tanxuanfood.service.predicate.ProductPredicate;
import com.peter.tanxuanfood.type.StatusType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final JPAQueryFactory jpaQueryFactory;


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

    public Product handleCreateProduct(@Valid Product requestProduct) {
        if (this.productRepository.existsByName(requestProduct.getName()))
            throw new IdInValidException("Product already exists");
        Product product = new Product();
        product.setName(requestProduct.getName());
        product.setPrice(requestProduct.getPrice());
        product.setDescription(requestProduct.getDescription());
        product.setStockQuantity(requestProduct.getStockQuantity());
        product.setIsAvailable(requestProduct.getIsAvailable());
        return this.productRepository.save(product);
    }

    public Product handleUpdateProduct(Product requestProduct) {
        Product product = this.productRepository
                .findById(requestProduct.getId())
                .orElseThrow(() -> new IdInValidException(PRODUCT_EXISTS));
        product.setName(requestProduct.getName());
        product.setPrice(requestProduct.getPrice());
        product.setDescription(requestProduct.getDescription());
        product.setStockQuantity(requestProduct.getStockQuantity());
        product.setIsAvailable(requestProduct.getIsAvailable());
        return this.productRepository.save(product);
    }

    public void handleDeleteProduct(long id) {
        Product product = this.productRepository
                .findById(id)
                .orElseThrow(() -> new IdInValidException(PRODUCT_EXISTS));
        this.productRepository.delete(product);
    }

    public List<ProductDTO> fetchAllProductInCart(Cart cart) {
        Set<CartDetail> cartDetails = this.cartDetailRepository.findByCart(cart);
        List<ProductDTO> productDTOList = new ArrayList<>();
        if (cartDetails != null && !cartDetails.isEmpty()) {
            productDTOList = cartDetails
                    .stream()
                    .map(cartDetail -> modelMapper.map(cartDetail.getProduct(), ProductDTO.class))
                    .toList();
        }
        return productDTOList;
    }

    public ResAddProductDTO handleAddProductToCart(String email, long productId, long productQuantity) {
        User user = this.userService.handleGetUserByUserName(email);
        ResAddProductDTO resAddProductDTO = new ResAddProductDTO();
        ResAddProductDTO.ResCartDTO respCartDTO = new ResAddProductDTO.ResCartDTO();


        // kiểm tra xem nó có Cart chưa
        Cart currentCart = this.cartRepository.findByUser(user);
        if (currentCart == null) {
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

        // Nếu chưa có trong giỏ hàng
        if (oldCartDetail == null) {
            cartDetail.setCart(currentCart);
            cartDetail.setProduct(product);
            cartDetail.setQuantity(productQuantity);
            cartDetail.setPrice(product.getPrice());

            // update Cart
            long s = currentCart.getSum() + 1;
            currentCart.setSum(s);
            this.cartDetailRepository.save(cartDetail);

            // Load lại Cart
            currentCart = this.cartRepository
                    .findById(currentCart.getId())
                    .orElse(currentCart);
        } else {
            oldCartDetail.setQuantity(oldCartDetail.getQuantity() + productQuantity);
            this.cartDetailRepository.save(oldCartDetail);
        }
        respCartDTO.setSum(currentCart.getSum());


        respCartDTO.setItems(this.fetchAllProductInCart(currentCart));
        resAddProductDTO.setCart(respCartDTO);
        return resAddProductDTO;
    }

    public void handleDeleteProductInCart(long id) {
        CartDetail cartDetail = this.cartDetailRepository
                .findById(id)
                .orElseThrow(() -> new IdInValidException("Product is not in Cart"));
        Cart cart = cartDetail.getCart();
        this.cartDetailRepository.deleteById(id);
        long s = cart.getSum();
        // Nếu trong giỏ hàng có nhiều sp
        if (s > 1) {
            s -= 1;
            cart.setSum(s);
            this.cartRepository.save(cart);
        } else { // Nếu giỏ hàng chỉ có 1 sản phẩm
            this.cartRepository.delete(cart);
        }
    }


    public CheckOutResponse preCheckOut(String email) {
        User user = this.userService.handleGetUserByUserName(email);
        if (user == null) throw new IdInValidException("User does not log in");
        CheckOutResponse checkOutResponse = new CheckOutResponse();
        CheckOutResponse.Client client = modelMapper.map(user, CheckOutResponse.Client.class);
        checkOutResponse.setClient(client);

        Cart cart = this.cartRepository.findByUser(user);
        if (cart == null) throw new IdInValidException("No product in cart");
        CheckOutResponse.PreCheckOutResponse preCheckOutResponse = new CheckOutResponse.PreCheckOutResponse();
        preCheckOutResponse.setSum(cart.getSum());

        Set<CartDetail> cartDetails = cart.getCartDetails();
        preCheckOutResponse.setItems(this.fetchAllProductInCart(cart));
        checkOutResponse.setCart(preCheckOutResponse);

        double totalPrice = 0;
        for (CartDetail cartDetail : cartDetails) {
            totalPrice += cartDetail.getPrice() * cartDetail.getQuantity();
        }
        checkOutResponse.setTotalPrice(totalPrice);

        return checkOutResponse;
    }

    public boolean checkProductQuantityInStock(CartDetail cartDetail) {
        Product product = cartDetail.getProduct();
        return product.getStockQuantity() >= cartDetail.getQuantity();
    }

    public CheckOutResponse handleCheckOut(String email, OrderInformationRequest orderInformationRequest) {
        User user = this.userService.handleGetUserByUserName(email);
        if (user == null) throw new Unauthenticated("User does not log in");
        CheckOutResponse checkOutResponse = new CheckOutResponse();
        CheckOutResponse.Client client = modelMapper.map(user, CheckOutResponse.Client.class);
        checkOutResponse.setClient(client);

        Cart cart = this.cartRepository.findByUser(user);
        if (cart == null) throw new IdInValidException("No product in cart");
        CheckOutResponse.PreCheckOutResponse preCheckOutResponse = new CheckOutResponse.PreCheckOutResponse();
        preCheckOutResponse.setSum(cart.getSum());

        Set<CartDetail> cartDetails = cart.getCartDetails();
        preCheckOutResponse.setItems(this.fetchAllProductInCart(cart));
        checkOutResponse.setCart(preCheckOutResponse);

        double totalPrice = 0;
        for (CartDetail cartDetail : cartDetails) {
            totalPrice += cartDetail.getPrice() * cartDetail.getQuantity();
        }

        Order order = modelMapper.map(orderInformationRequest, Order.class);
        order.setTotalPrice(totalPrice);
        order.setStatus(StatusType.PENDING);
        order.setUser(user);
        this.orderRepository.save(order);

        for (CartDetail cartDetail : cartDetails) {
            OrderDetail orderDetail = new OrderDetail();
            Product product = cartDetail.getProduct();

            orderDetail.setProduct(product);
            orderDetail.setPrice(cartDetail.getPrice());
            orderDetail.setQuantity(cartDetail.getQuantity());

            // Check xem sản phẩm trong trong kho có còn đủ mua cho khách hàng đó không
            if (!this.checkProductQuantityInStock(cartDetail)) throw new IdInValidException("Product is sold out");
            // update quantity
            product.setStockQuantity(product.getStockQuantity() - cartDetail.getQuantity());
            this.productRepository.save(product);

            orderDetail.setOrder(order);
            this.orderDetailRepository.save(orderDetail);
        }

        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        checkOutResponse.setOrder(orderDTO);

        // Sau khi set các giá trị xong thì xóa cart và cartDetail
        // Xóa cartDetails trước
        for (CartDetail cartDetail : cartDetails) {
            this.cartDetailRepository.deleteById(cartDetail.getId());
        }

        // Xóa cart sau
        this.cartRepository.delete(cart);

        return checkOutResponse;
    }

    public ResultPaginationDTO searchProduct(ProductFilter productFilter, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder
                .and(ProductPredicate.containsName(productFilter.getName()))
                .and(ProductPredicate.comparePrice(productFilter.getMinPrice(), productFilter.getMaxPrice()));

        List<Product> listProductFiltered = jpaQueryFactory
                .selectFrom(QProduct.product)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Page<Product> productPage = new PageImpl<>(listProductFiltered, pageable, listProductFiltered.size());
        Page<ProductDTO> productDTOPage = productPage.map(element -> modelMapper.map(element, ProductDTO.class));
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(productDTOPage.getTotalPages());
        meta.setTotal(productDTOPage.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setData(productDTOPage.getContent());
        return resultPaginationDTO;
    }


}
