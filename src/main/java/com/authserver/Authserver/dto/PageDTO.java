package com.authserver.Authserver.dto;

import java.util.List;

public class PageDTO<T> {
    private List<T> items;
    private long totalItems;

    public PageDTO(List<T> items, long totalItems) {
        this.items = items;
        this.totalItems = totalItems;
    }

    public List<T> getItems() {
        return items;
    }

    public long getTotalItems() {
        return totalItems;
    }
}