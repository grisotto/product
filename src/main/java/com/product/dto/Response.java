package com.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(content = JsonInclude.Include.NON_NULL)
public class Response<T> {

    @JsonProperty("data")
    private List<T> data;

    @JsonProperty("pagination")
    private Pagination pagination;

    public Response() {
    }

    public Response(List<T> data, Pagination pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
