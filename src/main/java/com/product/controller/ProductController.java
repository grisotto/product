package com.product.controller;

import com.product.dto.Response;
import com.product.model.Product;
import com.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping({"/api", "/api/v1"})
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping("/products")
    public ResponseEntity<Response<Product>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @PageableDefault(page = 0, size = 3) Pageable pageable) {
        Response<Product> productProductResponse = service.search(name, description, pageable);
        return new ResponseEntity<>(productProductResponse, HttpStatus.OK);
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        return service.create(product);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") long id) {
        return service.get(id);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") long id, @RequestBody @Valid Product product) {
        return service.update(id, product);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") long id) {
        return service.delete(id);
    }
}
