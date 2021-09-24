package com.product.product.repository;

import com.product.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

    Page<Product> getAllByNameContaining(String name, Pageable pageable);

    Page<Product> getAllByDescriptionContaining(String description, Pageable pageable);

    Page<Product> getAllByNameContainingAndDescriptionContaining(String name, String descripton, Pageable pageable);


}
