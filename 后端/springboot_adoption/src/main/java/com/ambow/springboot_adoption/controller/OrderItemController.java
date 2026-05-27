package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.OrderItemService;
import com.ambow.springboot_adoption.vo.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderItem/")
@CrossOrigin
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    //查全部
    @GetMapping
    public Result selectAllOrderItem(@RequestParam("pageNum") int pageNum,
                                     @RequestParam("pageSize") int pageSize){
        IPage<OrderItem> page = orderItemService.selectAllOrderItemPage(pageNum,pageSize);
        PageBean<OrderItem> pageBean = new PageBean<>(page.getTotal(),page.getRecords());
        return Result.success(pageBean);
    }
    @GetMapping("{userId}")
    public Result selectOrderByUserId(
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize,
            @PathVariable("userId") Integer userId,
            @RequestParam(required = false) Integer status) {
        // 调用服务层时传入status
        IPage<OrderItem> page = orderItemService.selectAllOrderItemByUserIdPage(pageNum, pageSize, userId,status);
        PageBean<OrderItem> pageBean = new PageBean<>(page.getTotal(), page.getRecords());
        return Result.success(pageBean);
    }

   /* //根据userId查询订单
    @GetMapping("{userId}")
    public Result selectOrderByUserId(@PathVariable("userId") Integer userId){
        List<OrderItem> result = orderItemService.getOrderItemsByUserId(userId);
        if ( result != null){
            return Result.success(result);
        }else{
            return Result.error("未查询到该条数据");
        }
    }*/

    //根据订单id查询订单详情
    @GetMapping("order/{orderId}")
    public Result selectOrderByOrderId(@PathVariable("orderId") Integer orderId){
        List<OrderItem> result = orderItemService.getOrderByOrderId(orderId);
        if (result != null ){
            return Result.success(result);
        }else{
            return Result.error("未查询到该订单详情");
        }
    }
    //添加
    @PostMapping("addOrderItem")
    public Result addOrderItem(@RequestBody OrderItem orderItem){
        int result = orderItemService.addOrderItem(orderItem);
        if (result > 0 ){
            return Result.success(result);
        }else {
            return Result.error("新增宠物用品失败");
        }
    }
    //删除
    @DeleteMapping("{orderItemId}")
    public Result deleteOrderItem(@PathVariable("orderItemId") Integer orderItemId){
        int result = orderItemService.deleteOrderItem(orderItemId);
        if (result > 0 ){
            return Result.success(result);
        }else {
            return Result.error("删除宠物用品失败");
        }
    }
    //修改
    @PatchMapping("updateOrderItem")
    public Result updateOrderItem(@RequestBody OrderItem orderItem){
        int result = orderItemService.updateOrderItem(orderItem);
        if (result > 0 ){
            return Result.success(result);
        }else {
            return Result.error("修改宠物用品失败");
        }
    }
    //模糊查询
    @GetMapping("search")
    public Result search(@RequestParam("keyword") String keyword){
        List<OrderItem> result = orderItemService.findOrderItemByKeyword(keyword);
        if (result != null){
            return Result.success(result);
        }else {
            return Result.error("未找到相关信息");
        }

    }


}
