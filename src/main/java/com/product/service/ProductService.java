package com.product.service;

import com.product.dto.Pagination;
import com.product.dto.Response;
import com.product.model.Product;
import com.product.repository.ProductRepository;
import com.product.repository.ProductWithDescriptionLikeSpecification;
import com.product.repository.ProductWithNameLikeSpecification;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Response<Product> search(String name, String description, Pageable pageable) {
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

    public ResponseEntity<Product> create(Product product) {
        try {
            var productSaved = repository
                    .save(new Product(product.getName(), product.getDescription()));
            return new ResponseEntity<>(productSaved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<HttpStatus> delete(long id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<Product> get(long id) {
        Optional<Product> product = repository.findById(id);
        return product.map(value ->
                        new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<Product> update(long id, Product product) {
        return repository.findById(id).map(value ->
        {
            value.setName(product.getName());
            value.setDescription(product.getDescription());
            return new ResponseEntity<>(repository.save(value), HttpStatus.OK);

        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
