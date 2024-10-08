package com.peter.tanxuanfood.service;

import com.peter.tanxuanfood.domain.Meta;
import com.peter.tanxuanfood.domain.Product;
import com.peter.tanxuanfood.domain.dto.ResultPaginationDTO;
import com.peter.tanxuanfood.exception.IdInValidException;
import com.peter.tanxuanfood.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private static final String PRODUCT_EXISTS = "Product does not exist";

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
}
