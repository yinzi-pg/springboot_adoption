package com.ambow.springboot_adoption.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public class PageBean<T> {

    private List<T> items;
    private Long total;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public PageBean(List<T> items, Long total) {
        this.items = items;
        this.total = total;
    }

    public PageBean() {
    }
}
