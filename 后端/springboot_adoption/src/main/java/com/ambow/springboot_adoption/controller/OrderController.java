package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.OrderService;
import com.ambow.springboot_adoption.vo.Order;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/")
    public Result<PageBean<Order>> getOrderList(
            @RequestParam(defaultValue = "1", required = false) Integer page, 
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(defaultValue = "1", required = false) Integer pageNum) {
        try {
            Integer actualPage = (page != null && page != 1) ? page : (pageNum != null ? pageNum : 1);
            return Result.success(orderService.getOrderList(actualPage, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/selectOrderById/{userId}")
    public Result<List<Order>> selectOrderById(@PathVariable Integer userId) {
        try {
            return Result.success(orderService.selectOrderById(userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/addOrder")
    public Result orderAddService(@RequestBody Order orderModel) {
        try {
            orderService.orderAddService(orderModel);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{orderId}")
    public Result orderDeleteService(@PathVariable Integer orderId) {
        try {
            orderService.orderDeleteService(orderId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PatchMapping("/updateOrder")
    public Result orderUpdateService(@RequestBody Order orderModel) {
        try {
            orderService.orderUpdateService(orderModel);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/search")
    public Result<PageBean<Order>> searchOrderService(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1", required = false) Integer page, 
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(defaultValue = "1", required = false) Integer pageNum) {
        try {
            Integer actualPage = (page != null && page != 1) ? page : (pageNum != null ? pageNum : 1);
            return Result.success(orderService.searchOrderService(keyword, actualPage, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
