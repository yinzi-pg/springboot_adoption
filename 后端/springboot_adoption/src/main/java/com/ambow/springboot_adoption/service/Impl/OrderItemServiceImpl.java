package com.ambow.springboot_adoption.service.impl;

import com.ambow.springboot_adoption.dao.OrderItemMapper;
import com.ambow.springboot_adoption.service.OrderItemService;
import com.ambow.springboot_adoption.vo.OrderItem;
import com.ambow.springboot_adoption.vo.PageBean;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Override
    public PageBean<OrderItem> getOrderItemList(Integer pageNum, Integer pageSize) {
        Page<OrderItem> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        Page<OrderItem> resultPage = this.page(page);
        return new PageBean<>(resultPage.getTotal(), resultPage.getRecords());
    }

    @Override
    public PageBean<OrderItem> getOrderByUserId(Integer userId, Integer status, Integer pageNum, Integer pageSize) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        Page<OrderItem> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        Page<OrderItem> resultPage = this.page(page, queryWrapper);
        return new PageBean<>(resultPage.getTotal(), resultPage.getRecords());
    }

    @Override
    public List<OrderItem> getOrderByOrderId(String orderIdParam) {
        if (orderIdParam == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        // 处理前端传递过来的可能带有前缀 '{' 的错字情况
        String orderIdStr = orderIdParam.replace("{", "");
        Integer orderId = Integer.valueOf(orderIdStr);
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrderItemService(OrderItem orderItemModel) {
        if (orderItemModel == null || orderItemModel.getProductId() == null) {
            throw new IllegalArgumentException("购物车信息和商品ID不能为空");
        }
        if (orderItemModel.getQuantity() != null && orderItemModel.getQuantity() < 0) {
            throw new IllegalArgumentException("商品数量不能为负数");
        }
        if (orderItemModel.getPrice() != null && orderItemModel.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("商品价格不能小于0");
        }
        
        // 查找购物车中是否已存在该商品（status=0）
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", orderItemModel.getUserId())
                    .eq("product_id", orderItemModel.getProductId())
                    .eq("status", 0);
        
        OrderItem existingItem = this.getOne(queryWrapper);
        if (existingItem != null) {
            // 已存在，数量累加
            Integer newQuantity = existingItem.getQuantity() + (orderItemModel.getQuantity() != null ? orderItemModel.getQuantity() : 1);
            existingItem.setQuantity(newQuantity);
            this.updateById(existingItem);
        } else {
            // 不存在，新增记录
            orderItemModel.setOrderId(null);
            orderItemModel.setStatus(0);
            if (orderItemModel.getQuantity() == null) {
                orderItemModel.setQuantity(1);
            }
            this.save(orderItemModel);
        }
    }

    @Override
    public void updateOrderItemService(OrderItem orderItemModel) {
        if (orderItemModel == null || orderItemModel.getOrderItemId() == null) {
            throw new IllegalArgumentException("购物车项信息或ID不能为空");
        }
        if (orderItemModel.getQuantity() != null && orderItemModel.getQuantity() < 0) {
            throw new IllegalArgumentException("商品数量不能为负数");
        }
        this.updateById(orderItemModel);
    }

    @Override
    public void deleteOrderItemService(Integer orderItemId) {
        if (orderItemId == null) {
            throw new IllegalArgumentException("购物车项ID不能为空");
        }
        this.removeById(orderItemId);
    }

    @Override
    public PageBean<OrderItem> searchAdoptionService(String keyword, Integer pageNum, Integer pageSize) {
        Page<OrderItem> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like("product_name", keyword);
        }
        Page<OrderItem> resultPage = this.page(page, queryWrapper);
        return new PageBean<>(resultPage.getTotal(), resultPage.getRecords());
    }
}
