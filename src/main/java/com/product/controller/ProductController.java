package com.product.controller;

import com.product.dto.Response;
import com.product.model.Product;
import com.product.repository.ProductRepository;
import com.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping({"/api", "/api/v1"})
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductService service;

    @GetMapping("/products")
    public ResponseEntity<Response<Product>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @PageableDefault(page = 0, size = 3) Pageable pageable) {
        Response<Product> productProductResponse = service.searchProduct(name, description, pageable);
        return new ResponseEntity<>(productProductResponse, HttpStatus.OK);
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        try {
            var _product = repository
                    .save(new Product(product.getName(), product.getDescription()));
            return new ResponseEntity<>(_product, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") long id) {
        Optional<Product> product = repository.findById(id);

        return product.map(value ->
                        new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") long id, @RequestBody @Valid Product product) {
        Optional<Product> oldProduct = repository.findById(id);
        return oldProduct.map(value ->
        {
            value.setName(product.getName());
            value.setDescription(product.getDescription());
            return new ResponseEntity<>(repository.save(value), HttpStatus.OK);

        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") long id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
