package com.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.Pagination;
import com.product.dto.Response;
import com.product.model.Product;
import com.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Test
    public void givenProducts_whenGetProducts_thenContentAndStatus200() throws Exception {

        var products = List.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));
        PageImpl<Product> pageProducts = new PageImpl<>(products);
        when(service.search(any(), any(), any(Pageable.class)))
                .thenReturn(new Response<>(products,
                        new Pagination(pageProducts.getNumber(), pageProducts.getTotalElements(), pageProducts.getTotalPages())));

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("pagination.totalItems", is(1)))
                .andExpect(jsonPath("pagination.totalPages", is(1)))
                .andExpect(jsonPath("pagination.currentPage", is(0)))
                .andExpect(jsonPath("$['data'][0].id", is(0)))
                .andExpect(jsonPath("$['data'][0].name", is(products.get(0).getName())))
                .andExpect(jsonPath("$['data'][0].description", is(products.get(0).getDescription())));
    }


    @Test
    public void givenProducts_whenGetProducts_thenNotContentAndStatus204() throws Exception {
        when(service.search(any(), any(), any(Pageable.class)))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NO_CONTENT, "Products Not Found"));

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void givenNone_whenGetProductsAndPageAndSizeParameters_thenEvaluatePageParameters() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(service).search(any(), any(), pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertEquals(1, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void givenProducts_whenGetProductsAndFilterName_thenContentAndStatus200() throws Exception {

        var products = List.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));
        PageImpl<Product> pageProducts = new PageImpl<>(products);
        when(service.search(any(), any(), any(Pageable.class)))
                .thenReturn(new Response<>(products,
                        new Pagination(pageProducts.getNumber(), pageProducts.getTotalElements(), pageProducts.getTotalPages())));
        mockMvc.perform(get("/api/products")
                        .param("name", "Curto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("pagination.totalItems", is(1)))
                .andExpect(jsonPath("pagination.totalPages", is(1)))
                .andExpect(jsonPath("pagination.currentPage", is(0)))
                .andExpect(jsonPath("$['data'][0].id", is(0)))
                .andExpect(jsonPath("$['data'][0].name", is(products.get(0).getName())))
                .andExpect(jsonPath("$['data'][0].description", is(products.get(0).getDescription())));


        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        verify(service).search(nameCaptor.capture(), descriptionCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Curto", nameCaptor.getValue());
        assertNull(descriptionCaptor.getValue());
    }

    @Test
    public void givenNone_whenGetProductsAndFilterName_thenNotContentAndStatus204() throws Exception {
        when(service.search(any(), any(), any(Pageable.class)))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NO_CONTENT, "Products Not Found"));

        mockMvc.perform(get("/api/products")
                        .param("name", "Curto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").doesNotExist());

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        verify(service).search(nameCaptor.capture(), descriptionCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Curto", nameCaptor.getValue());
    }

    @Test
    public void givenProducts_whenGetProductsAndFilterDescription_thenContentAndStatus200() throws Exception {

        var products = List.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));
        PageImpl<Product> pageProducts = new PageImpl<>(products);
        when(service.search(any(), any(), any(Pageable.class)))
                .thenReturn(new Response<>(products,
                        new Pagination(pageProducts.getNumber(), pageProducts.getTotalElements(), pageProducts.getTotalPages())));
        mockMvc.perform(get("/api/products")
                        .param("description", "Vaqueiro")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("pagination.totalItems", is(1)))
                .andExpect(jsonPath("pagination.totalPages", is(1)))
                .andExpect(jsonPath("pagination.currentPage", is(0)))
                .andExpect(jsonPath("$['data'][0].id", is(0)))
                .andExpect(jsonPath("$['data'][0].name", is(products.get(0).getName())))
                .andExpect(jsonPath("$['data'][0].description", is(products.get(0).getDescription())));

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        verify(service).search(nameCaptor.capture(), descriptionCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Vaqueiro", descriptionCaptor.getValue());
    }

    @Test
    public void givenNone_whenGetProductsAndFilterDescription_thenNotContentAndStatus204() throws Exception {
        when(service.search(any(), any(), any(Pageable.class)))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NO_CONTENT, "Products Not Found"));

        mockMvc.perform(get("/api/products")
                        .param("description", "Vaqueiro")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").doesNotExist());

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        verify(service).search(nameCaptor.capture(), descriptionCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Vaqueiro", descriptionCaptor.getValue());
    }

    @Test
    public void givenProducts_whenGetProductsAndFilterNameAndDescription_thenContentAndStatus200() throws Exception {

        var products = List.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));

        PageImpl<Product> pageProducts = new PageImpl<>(products);
        when(service.search(any(), any(), any(Pageable.class)))
                .thenReturn(new Response<>(products,
                        new Pagination(pageProducts.getNumber(), pageProducts.getTotalElements(), pageProducts.getTotalPages())));
        mockMvc.perform(get("/api/products")
                        .param("name", "Curto")
                        .param("description", "Vaqueiro")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("pagination.totalItems", is(1)))
                .andExpect(jsonPath("pagination.totalPages", is(1)))
                .andExpect(jsonPath("pagination.currentPage", is(0)))
                .andExpect(jsonPath("$['data'][0].id", is(0)))
                .andExpect(jsonPath("$['data'][0].name", is(products.get(0).getName())))
                .andExpect(jsonPath("$['data'][0].description", is(products.get(0).getDescription())));
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        verify(service).search(nameCaptor.capture(), descriptionCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Curto", nameCaptor.getValue());
        assertEquals("Vaqueiro", descriptionCaptor.getValue());
    }

    @Test
    public void givenNone_whenGetProductsAndFilterNameAndDescription_thenNotContentAndStatus204() throws Exception {
        when(service.search(any(), any(), any(Pageable.class)))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NO_CONTENT, "Products Not Found"));

        mockMvc.perform(get("/api/products")
                        .param("name", "Curto")
                        .param("description", "Vaqueiro")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").doesNotExist());

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        verify(service).search(nameCaptor.capture(), descriptionCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Curto", nameCaptor.getValue());
        assertEquals("Vaqueiro", descriptionCaptor.getValue());
    }

    @Test
    public void givenProduct_whenCreateProduct_thenCreatAndStatus201() throws Exception {
        var product = new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto");
        when(service.create(any()))
                .thenReturn(new ResponseEntity<>(product, HttpStatus.CREATED));

        mockMvc.perform(post("/api/products")
                        .content(asJsonString(product))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andExpect(jsonPath("$.description", is(product.getDescription())));

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(service).create(productArgumentCaptor.capture());

        assertEquals(product.getName(), productArgumentCaptor.getValue().getName());
        assertEquals(product.getDescription(), productArgumentCaptor.getValue().getDescription());
    }

    @Test
    public void givenProductWithNullName_whenCreateProduct_thenErrorAndStatus400() throws Exception {
        var product = new Product("Vaqueiro Curto", null);

        mockMvc.perform(post("/api/products")
                        .content(asJsonString(product))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist());

    }

    @Test
    public void givenProductWithNullDescription_whenCreateProduct_thenErrorAndStatus400() throws Exception {
        var product = new Product(null, "Nuevo estilo de Vaqueiro curto");

        mockMvc.perform(post("/api/products")
                        .content(asJsonString(product))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist());

    }

    @Test
    public void givenProduct_whenGetProductById_thenResultAndStatus200() throws Exception {
        var product = new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto");
        when(service.get(anyLong()))
                .thenReturn(new ResponseEntity<>(product, HttpStatus.OK));

        mockMvc.perform(get("/api/products/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andExpect(jsonPath("$.description", is(product.getDescription())));

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(service).get(idCaptor.capture());

        assertEquals(0, idCaptor.getValue());
    }

    @Test
    public void givenNone_whenGetProductById_thenStatus404() throws Exception {
        when(service.get(anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/products/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(service).get(idCaptor.capture());

        assertEquals(0, idCaptor.getValue());
    }

    @Test
    public void givenProduct_whenUpdateProduct_thenResultAndStatus200() throws Exception {
        var newProduct = new Product("Vaqueiro Curto Verano", "Nuevo estilo de Vaqueiro curto Verano");

        when(service.update(anyLong(), any()))
                .thenReturn(new ResponseEntity<>(newProduct, HttpStatus.OK));

        mockMvc.perform(put("/api/products/{id}", 0)
                        .content(asJsonString(newProduct))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.name", is(newProduct.getName())))
                .andExpect(jsonPath("$.description", is(newProduct.getDescription())));

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(service).update(anyLong(), productArgumentCaptor.capture());

        assertEquals(newProduct.getName(), productArgumentCaptor.getValue().getName());
        assertEquals(newProduct.getDescription(), productArgumentCaptor.getValue().getDescription());
    }

    @Test
    public void givenNone_whenUpdateProduct_thenStatus404() throws Exception {
        var newProduct = new Product("Vaqueiro Curto Verano", "Nuevo estilo de Vaqueiro curto Verano");
        when(service.update(anyLong(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/products/{id}", 0)
                        .content(asJsonString(newProduct))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(service).update(idCaptor.capture(), productArgumentCaptor.capture());

        assertEquals(0, idCaptor.getValue());
        assertEquals(newProduct, productArgumentCaptor.getValue());

    }

    @Test
    public void givenProductWithNullName_whenUpdateProduct_thenErrorAndStatus404() throws Exception {
        var newProduct = new Product(null, "Nuevo estilo de Vaqueiro curto Verano");

        mockMvc.perform(put("/api/products/{id}", 0)
                        .content(asJsonString(newProduct))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void givenProductWithNullDescription_whenUpdateProduct_thenErrorAndStatus404() throws Exception {
        var newProduct = new Product("Vaqueiro curto", null);

        mockMvc.perform(put("/api/products/{id}", 0)
                        .content(asJsonString(newProduct))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void givenProduct_whenDeleteProduct_thenStatus204() throws Exception {
        when(service.delete(anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        mockMvc.perform(delete("/api/products/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(service).delete(idCaptor.capture());

        assertEquals(0, idCaptor.getValue());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}