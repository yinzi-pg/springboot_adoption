package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.Impl.CZServiceImpl;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.PublicInformation;
import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/")
@CrossOrigin
public class UserInformationManagementController {
    @Autowired
    private CZServiceImpl czService;


    //模糊查询
    @GetMapping("selectUserByKeyWord")
    public Result selectUserByKeyWord(@RequestParam("keyword") String keyword,
                                      @RequestParam("pageNum") int pageNum,
                                      @RequestParam("pageSize") int pageSize){
        IPage<User> result = czService.selectUserByKeyword(keyword,pageNum,pageSize);
        PageBean<User> pageBean = new PageBean<>(result.getTotal(), result.getRecords());
        return Result.success(pageBean);
    }


    //分页查询
    @GetMapping
    public Result selectUserByPage(@RequestParam("pageNum") int pageNum,
                                @RequestParam("pageSize") int pageSize){
        IPage<User> page = czService.selectUserByPage(pageNum,pageSize);
        PageBean<User> pageBean = new PageBean<>(page.getTotal(),page.getRecords());
        return Result.success(pageBean);
    }

    //修改
    @PatchMapping("updateUser")
    public Result updateUser(@RequestBody User user){
        int result = czService.updateUser(user);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.error("修改用户信息失败");
        }
    }

    //删除
    @DeleteMapping("{userId}")
    public Result deleteUser(@PathVariable("userId") Integer userId){
        int result = czService.deleteUser(userId);
        if (result > 0 ){
            return Result.success();
        }else {
            return Result.error("删除用户信息失败");
        }
    }
}
