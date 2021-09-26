package com.product.service;

import com.product.dto.Pagination;
import com.product.dto.Response;
import com.product.model.Product;
import com.product.repository.ProductRepository;
import com.product.repository.ProductWithDescriptionLikeSpecification;
import com.product.repository.ProductWithNameLikeSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Response<Product> searchProduct(String name, String description, Pageable pageable) {
        Page<Product> productsFound;
        try {
            var spec = Specification
                    .where(new ProductWithNameLikeSpecification(name))
                    .and(new ProductWithDescriptionLikeSpecification(description));

            productsFound = repository.findAll(spec, pageable);

        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Unexpect error", ex);
        }
        if (productsFound.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NO_CONTENT, "Products Not Found");
        }

        return new Response<>(productsFound.getContent(),
                new Pagination(productsFound.getNumber(), productsFound.getTotalElements(), productsFound.getTotalPages()));
    }
}
