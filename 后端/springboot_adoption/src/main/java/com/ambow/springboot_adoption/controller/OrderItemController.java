package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.OrderItemService;
import com.ambow.springboot_adoption.vo.OrderItem;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderItem")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/")
    public Result<PageBean<OrderItem>> getOrderItemList(
            @RequestParam(defaultValue = "1", required = false) Integer page, 
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(defaultValue = "1", required = false) Integer pageNum) {
        try {
            Integer actualPage = (page != null && page != 1) ? page : (pageNum != null ? pageNum : 1);
            return Result.success(orderItemService.getOrderItemList(actualPage, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public Result<PageBean<OrderItem>> getOrderByUserId(
            @PathVariable Integer userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1", required = false) Integer page, 
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(defaultValue = "1", required = false) Integer pageNum) {
        try {
            Integer actualPage = (page != null && page != 1) ? page : (pageNum != null ? pageNum : 1);
            return Result.success(orderItemService.getOrderByUserId(userId, status, actualPage, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 匹配前端错字路径：/orderItem/order/{123
    @GetMapping("/order/{orderIdParam}")
    public Result<List<OrderItem>> getOrderByOrderId(@PathVariable String orderIdParam) {
        try {
            return Result.success(orderItemService.getOrderByOrderId(orderIdParam));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/addOrderItem")
    public Result addOrderItemService(@RequestBody OrderItem orderItemModel) {
        try {
            orderItemService.addOrderItemService(orderItemModel);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    @PatchMapping("/updateOrderItem")
    public Result updateOrderItemService(@RequestBody OrderItem orderItemModel) {
        try {
            orderItemService.updateOrderItemService(orderItemModel);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{orderItemId}")
    public Result deleteOrderItemService(@PathVariable Integer orderItemId) {
        try {
            orderItemService.deleteOrderItemService(orderItemId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/search")
    public Result<PageBean<OrderItem>> searchAdoptionService(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1", required = false) Integer page, 
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(defaultValue = "1", required = false) Integer pageNum) {
        try {
            Integer actualPage = (page != null && page != 1) ? page : (pageNum != null ? pageNum : 1);
            return Result.success(orderItemService.searchAdoptionService(keyword, actualPage, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
