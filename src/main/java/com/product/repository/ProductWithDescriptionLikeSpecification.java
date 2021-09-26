package com.product.repository;

import com.product.model.Product;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ProductWithDescriptionLikeSpecification implements Specification<Product> {

    private final String description;

    public ProductWithDescriptionLikeSpecification(String description) {
        this.description = description;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (description == null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }
        return criteriaBuilder.like(
                root.get("description"), "%" + this.description + "%");
    }
}
