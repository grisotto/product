package com.product.service;

import com.product.dto.Response;
import com.product.model.Product;
import com.product.repository.ProductRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    public void givenProducts_whenSearch_thenSucess() throws Exception {
        try {
            var product = new Product("Vaqueiro Curto Verano", "Nuevo estilo de Vaqueiro curto Verano");
            PageImpl<Product> products = new PageImpl<>(List.of(product));
            when(repository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(products);

            Response<Product> search = service.search(null, null, Pageable.ofSize(3));
            assertNotNull(search);

        } catch (Exception exception) {
            fail("There should have been no exception");
        }

    }

    @Test
    public void givenProducts_whenSearchWithName_thenSucess() throws Exception {
        try {
            var product = new Product("Vaqueiro Curto Verano", "Nuevo estilo de Vaqueiro curto Verano");
            PageImpl<Product> products = new PageImpl<>(List.of(product));
            when(repository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(products);

            Response<Product> search = service.search("Vaqueiro", null, Pageable.ofSize(3));
            assertNotNull(search);

        } catch (Exception exception) {
            fail("There should have been no exception");
        }

    }
    @Test
    public void givenProducts_whenSearchWithDescription_thenSucess() throws Exception {
        try {
            var product = new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto Verano");
            PageImpl<Product> products = new PageImpl<>(List.of(product));
            when(repository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(products);

            Response<Product> search = service.search(null, "Verano", Pageable.ofSize(3));
            assertNotNull(search);

        } catch (Exception exception) {
            fail("There should have been no exception");
        }

    }

    @Test
    public void givenProducts_whenCreate_thenSucess() throws Exception {
        var newProduct = new Product("Vaqueiro Curto Verano", "Nuevo estilo de Vaqueiro curto Verano");
        when(repository.save(any()))
                .thenReturn(newProduct);

        try {
            ResponseEntity<Product> response = service.create(newProduct);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());

        } catch (Exception exception) {
            fail("There should have been no exception");
        }

    }

    @Test
    public void givenProducts_whenCreate_thenError() throws Exception {
        try {
            var newProduct = new Product("Vaqueiro Curto Verano", "Nuevo estilo de Vaqueiro curto Verano");
            when(repository.save(any()))
                    .thenThrow(new NullPointerException());

            ResponseEntity<Product> response = service.create(newProduct);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        } catch (Exception exception) {
            fail("There should have been no exception");
        }

    }

    @Test
    public void givenProducts_whenUpdate_thenSucess() throws Exception {
        var product = Optional.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));
        var newProduct = new Product("Vaqueiro Curto Verano", "Nuevo estilo de Vaqueiro curto Verano");
        when(repository.findById(anyLong()))
                .thenReturn(product);

        when(repository.save(any()))
                .thenReturn(newProduct);
        try {
            ResponseEntity<Product> response = service.update(0, newProduct);
            assertEquals(HttpStatus.OK, response.getStatusCode());

        } catch (Exception exception) {
            fail("There should have been no exception");
        }

    }

    @Test
    public void givenNone_whenUpdate_thenNotFound() throws Exception {
        var newProduct = new Product("Vaqueiro Curto Verano", "Nuevo estilo de Vaqueiro curto Verano");
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        try {
            ResponseEntity<Product> response = service.update(0, newProduct);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        } catch (Exception exception) {
            fail("There should have been no exception");
        }

    }

    @Test
    public void givenProducts_whenGet_thenSucess() throws Exception {
        var product = Optional.of(new Product("Vaqueiro Curto", "Nuevo estilo de Vaqueiro curto"));
        when(repository.findById(anyLong()))
                .thenReturn(product);
        try {
            ResponseEntity<Product> response = service.get(0);
            assertEquals(HttpStatus.OK, response.getStatusCode());

        } catch (Exception exception) {
            fail("There should have been no exception");
        }
    }

    @Test
    public void givenNone_whenGet_thenNotFound() throws Exception {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        try {
            ResponseEntity<Product> response = service.get(0);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        } catch (Exception exception) {
            fail("There should have been no exception");
        }
    }

    @Test
    public void givenProducts_whenDelete_thenNoContent() throws Exception {
        try {
            ResponseEntity<HttpStatus> response = service.delete(0);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
            verify(repository).deleteById(idCaptor.capture());

            assertEquals(0, idCaptor.getValue());

        } catch (Exception exception) {
            fail("There should have been no exception");
        }
    }

    @Test
    public void givenNone_whenDelete_thenNoContent() throws Exception {
        try {
            when(service.delete(anyLong())).thenThrow(new EmptyResultDataAccessException(0));

            ResponseEntity<HttpStatus> response = service.delete(0);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
            verify(repository).deleteById(idCaptor.capture());

            assertEquals(0, idCaptor.getValue());
        } catch (Exception exception) {
            fail("There should have been no exception");
        }
    }

    @Test
    public void givenNone_whenDelete_thenError() throws Exception {
        try {
            when(service.delete(anyLong())).thenThrow(new NullPointerException());

            ResponseEntity<HttpStatus> response = service.delete(0);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
            verify(repository).deleteById(idCaptor.capture());

            assertEquals(0, idCaptor.getValue());
        } catch (Exception exception) {
            fail("There should have been no exception");
        }
    }

}