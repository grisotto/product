package com.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(content = JsonInclude.Include.NON_NULL)
public class Pagination {

    @JsonProperty("currentPage")
    private int currentPage;

    @JsonProperty("totalItems")
    private long totalItems;

    @JsonProperty("totalPages")
    private int totalPages;

    public Pagination() {
    }

    public Pagination(int currentPage, long totalItems, int totalPages) {
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
