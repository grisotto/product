package com.product.repository;

import com.product.model.Product;
import org.hamcrest.collection.IsIn;
import org.hamcrest.core.IsNot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class ProductWithNameLikeSpecificationTest {

    @Autowired
    private ProductRepository repository;

    private Product productPantalon;

    private Product productVaqueiro;

    @Before
    public void init() {
        productPantalon = new Product("Pantalon bonito", "Nuevo pantalon 2021 verano");
        productVaqueiro = new Product("Vaqueiro curto", "Vaqueiro classico verano");
        repository.save(productPantalon);
        repository.save(productVaqueiro);
    }

    @Test
    public void givenName_whenFindAllProductsAndFilterName_thenSucess() {
        var spec = new ProductWithNameLikeSpecification("bonito");

        List<Product> results = repository.findAll(spec);

        assertEquals(1, results.size());
        assertThat(productPantalon, IsIn.in(results));
        assertThat(productVaqueiro, IsNot.not(results));
    }

    @Test
    public void givenName_whenFindAllProductsAndFilterName_thenNotFound() {
        var spec = new ProductWithNameLikeSpecification("bonita");

        List<Product> results = repository.findAll(spec);

        assertEquals(0, results.size());
        assertThat(productVaqueiro, IsNot.not(results));
        assertThat(productPantalon, IsNot.not(results));
    }

}