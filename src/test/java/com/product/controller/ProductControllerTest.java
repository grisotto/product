package com.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.model.Product;
import com.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @Test
    public void givenProducts_whenGetProducts_thenContentAndStatus200() throws Exception {

        var products = List.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));
        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(products));
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("totalItems", is(1)))
                .andExpect(jsonPath("totalPages", is(1)))
                .andExpect(jsonPath("currentPage", is(0)))
                .andExpect(jsonPath("$['data'][0].id", is(0)))
                .andExpect(jsonPath("$['data'][0].name", is(products.get(0).getName())))
                .andExpect(jsonPath("$['data'][0].description", is(products.get(0).getDescription())));
    }


    @Test
    public void givenProducts_whenGetProducts_thenNotContentAndStatus204() throws Exception {
        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

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
        verify(productRepository).findAll(pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertEquals(1, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    public void givenProducts_whenGetProductsAndFilterName_thenContentAndStatus200() throws Exception {

        var products = List.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));
        when(productRepository.getAllByNameContaining(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(products));
        mockMvc.perform(get("/api/products")
                        .param("name", "Curto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("totalItems", is(1)))
                .andExpect(jsonPath("totalPages", is(1)))
                .andExpect(jsonPath("currentPage", is(0)))
                .andExpect(jsonPath("$['data'][0].id", is(0)))
                .andExpect(jsonPath("$['data'][0].name", is(products.get(0).getName())))
                .andExpect(jsonPath("$['data'][0].description", is(products.get(0).getDescription())));


        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        verify(productRepository).getAllByNameContaining(nameCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Curto", nameCaptor.getValue());
    }

    @Test
    public void givenNone_whenGetProductsAndFilterName_thenNotContentAndStatus204() throws Exception {
        when(productRepository.getAllByNameContaining(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/products")
                        .param("name", "Curto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").doesNotExist());

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        verify(productRepository).getAllByNameContaining(nameCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Curto", nameCaptor.getValue());
    }

    @Test
    public void givenProducts_whenGetProductsAndFilterDescription_thenContentAndStatus200() throws Exception {

        var products = List.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));
        when(productRepository.getAllByDescriptionContaining(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(products));
        mockMvc.perform(get("/api/products")
                        .param("description", "Vaqueiro")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("totalItems", is(1)))
                .andExpect(jsonPath("totalPages", is(1)))
                .andExpect(jsonPath("currentPage", is(0)))
                .andExpect(jsonPath("$['data'][0].id", is(0)))
                .andExpect(jsonPath("$['data'][0].name", is(products.get(0).getName())))
                .andExpect(jsonPath("$['data'][0].description", is(products.get(0).getDescription())));

        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        verify(productRepository).getAllByDescriptionContaining(descriptionCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Vaqueiro", descriptionCaptor.getValue());
    }

    @Test
    public void givenNone_whenGetProductsAndFilterDescription_thenNotContentAndStatus204() throws Exception {
        when(productRepository.getAllByDescriptionContaining(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/products")
                        .param("description", "Vaqueiro")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").doesNotExist());

        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        verify(productRepository).getAllByDescriptionContaining(descriptionCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Vaqueiro", descriptionCaptor.getValue());
    }

    @Test
    public void givenProducts_whenGetProductsAndFilterNameAndDescription_thenContentAndStatus200() throws Exception {

        var products = List.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));

        when(productRepository.getAllByNameContainingAndDescriptionContaining(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(products));
        mockMvc.perform(get("/api/products")
                        .param("name", "Curto")
                        .param("description", "Vaqueiro")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("totalItems", is(1)))
                .andExpect(jsonPath("totalPages", is(1)))
                .andExpect(jsonPath("currentPage", is(0)))
                .andExpect(jsonPath("$['data'][0].id", is(0)))
                .andExpect(jsonPath("$['data'][0].name", is(products.get(0).getName())))
                .andExpect(jsonPath("$['data'][0].description", is(products.get(0).getDescription())));

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        verify(productRepository).getAllByNameContainingAndDescriptionContaining(nameCaptor.capture(), descriptionCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Curto", nameCaptor.getValue());
        assertEquals("Vaqueiro", descriptionCaptor.getValue());
    }

    @Test
    public void givenNone_whenGetProductsAndFilterNameAndDescription_thenNotContentAndStatus204() throws Exception {
        when(productRepository.getAllByNameContainingAndDescriptionContaining(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/products")
                        .param("name", "Curto")
                        .param("description", "Vaqueiro")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").doesNotExist());

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        verify(productRepository).getAllByNameContainingAndDescriptionContaining(nameCaptor.capture(), descriptionCaptor.capture(),
                ArgumentCaptor.forClass(Pageable.class).capture());

        assertEquals("Curto", nameCaptor.getValue());
        assertEquals("Vaqueiro", descriptionCaptor.getValue());
    }

    @Test
    public void givenProduct_whenCreateProduct_thenCreatAndStatus201() throws Exception {
        var product = new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto");
        when(productRepository.save(any()))
                .thenReturn(product);

        mockMvc.perform(post("/api/products")
                        .content(asJsonString(product))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andExpect(jsonPath("$.description", is(product.getDescription())));

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());

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
        var product = Optional.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));
        when(productRepository.findById(any()))
                .thenReturn(product);

        mockMvc.perform(get("/api/products/0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.name", is(product.get().getName())))
                .andExpect(jsonPath("$.description", is(product.get().getDescription())));

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository).findById(idCaptor.capture());

        assertEquals(0, idCaptor.getValue());
    }

    @Test
    public void givenNone_whenGetProductById_thenStatus404() throws Exception {
        Optional<Product> product = Optional.empty();
        when(productRepository.findById(any()))
                .thenReturn(product);

        mockMvc.perform(get("/api/products/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository).findById(idCaptor.capture());

        assertEquals(0, idCaptor.getValue());
    }

    @Test
    public void givenProduct_whenUpdateProduct_thenResultAndStatus200() throws Exception {
        var oldProduct = Optional.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));
        var newProduct = new Product("Vaqueiro Curto Verano", "Nuevo estilo de Vaqueiro curto Verano");
        when(productRepository.findById(any()))
                .thenReturn(oldProduct);

        when(productRepository.save(any()))
                .thenReturn(newProduct);

        mockMvc.perform(put("/api/products/{id}", 0)
                        .content(asJsonString(newProduct))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.name", is(newProduct.getName())))
                .andExpect(jsonPath("$.description", is(newProduct.getDescription())));

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository).findById(idCaptor.capture());

        assertEquals(0, idCaptor.getValue());

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());

        assertEquals(newProduct.getName(), productArgumentCaptor.getValue().getName());
        assertEquals(newProduct.getDescription(), productArgumentCaptor.getValue().getDescription());
    }

    @Test
    public void givenNone_whenUpdateProduct_thenStatus404() throws Exception {
        Optional<Product> oldProduct = Optional.empty();
        var newProduct = new Product("Vaqueiro Curto Verano", "Nuevo estilo de Vaqueiro curto Verano");
        when(productRepository.findById(any()))
                .thenReturn(oldProduct);

        mockMvc.perform(put("/api/products/{id}", 0)
                        .content(asJsonString(newProduct))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository).findById(idCaptor.capture());

        assertEquals(0, idCaptor.getValue());

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
        mockMvc.perform(delete("/api/products/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository).deleteById(idCaptor.capture());

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