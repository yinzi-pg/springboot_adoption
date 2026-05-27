package com.ambow.springboot_adoption.service.impl;

import com.ambow.springboot_adoption.dao.OrderItemMapper;
import com.ambow.springboot_adoption.dao.OrderMapper;
import com.ambow.springboot_adoption.dao.PetSuppliesMapper;
import com.ambow.springboot_adoption.service.OrderService;
import com.ambow.springboot_adoption.vo.Order;
import com.ambow.springboot_adoption.vo.OrderItem;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.PetSupplies;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PetSuppliesMapper petSuppliesMapper;

    @Override
    public PageBean<Order> getOrderList(Integer pageNum, Integer pageSize) {
        Page<Order> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        Page<Order> resultPage = this.page(page);
        return new PageBean<>(resultPage.getTotal(), resultPage.getRecords());
    }

    @Override
    public List<Order> selectOrderById(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void orderAddService(Order orderModel) {
        if (orderModel == null || orderModel.getUserId() == null) {
            throw new IllegalArgumentException("订单信息和用户ID不能为空");
        }
        
        // 查找用户购物车中待结算的商品 (status = 0)
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", orderModel.getUserId()).eq("status", 0);
        List<OrderItem> cartItems = orderItemMapper.selectList(queryWrapper);
        
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("购物车为空，无法提交订单");
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // 1. 校验库存并计算总金额
        for (OrderItem item : cartItems) {
            PetSupplies petSupplies = petSuppliesMapper.selectById(item.getProductId());
            if (petSupplies == null) {
                throw new RuntimeException("商品 [" + item.getProductName() + "] 不存在");
            }
            if (petSupplies.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品 [" + item.getProductName() + "] 库存不足，当前库存：" + petSupplies.getStock());
            }
            // 累加总金额 = 单价 * 数量
            totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        
        // 2. 生成主订单
        orderModel.setTotalAmount(totalAmount);
        orderModel.setCreateTime(LocalDateTime.now());
        if (orderModel.getStatus() == null) {
            orderModel.setStatus("已支付"); // 或者待支付，取决于业务逻辑
        }
        this.save(orderModel); // 此时会回填 orderId
        
        // 3. 更新购物车状态并扣减库存
        for (OrderItem item : cartItems) {
            // 更新购物车状态
            item.setOrderId(orderModel.getOrderId());
            item.setStatus(1); // 1-已下单
            orderItemMapper.updateById(item);
            
            // 扣减库存
            PetSupplies petSupplies = petSuppliesMapper.selectById(item.getProductId());
            petSupplies.setStock(petSupplies.getStock() - item.getQuantity());
            petSuppliesMapper.updateById(petSupplies);
        }
    }

    @Override
    public void orderDeleteService(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        this.removeById(orderId);
    }

    @Override
    public void orderUpdateService(Order orderModel) {
        if (orderModel == null || orderModel.getOrderId() == null) {
            throw new IllegalArgumentException("订单信息或ID不能为空");
        }
        if (orderModel.getTotalAmount() != null && orderModel.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("总金额不能小于0");
        }
        this.updateById(orderModel);
    }

    @Override
    public PageBean<Order> searchOrderService(String keyword, Integer pageNum, Integer pageSize) {
        Page<Order> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like("user_name", keyword)
                        .or().like("user_phone", keyword)
                        .or().like("status", keyword)
                        .or().like("remark", keyword);
        }
        Page<Order> resultPage = this.page(page, queryWrapper);
        return new PageBean<>(resultPage.getTotal(), resultPage.getRecords());
    }
}
