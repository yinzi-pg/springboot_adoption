package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.dao.VolunteerMapper;
import com.ambow.springboot_adoption.service.UserService;
import com.ambow.springboot_adoption.service.VolunteerService;
import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.User;
import com.ambow.springboot_adoption.vo.Volunteer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*  注册
*   前端传递用户对象
*   包含
*   userName: '', 用户名字
    userPassword: '', 用户密码
    userGender: 'male', 用户性别
    userAge: '',   用户年龄
    userPhone: '', 用户手机号码
    userEmail: '',  用户邮箱
    userRole: '',  用户角色
    avatar: ''     头像地址
    * 封装成用户对象传递到service层
*   进行参数非空判断
*   判断名字是否重复
*   判断年龄是否合法
*   电话号码是否合法
*   邮箱是否合法
*   调用dao层的添加方法
*   判断影响行数
*   正常给Result设值返回
*    Controller 将Result返回前端
*
*
* */

@RestController
@RequestMapping("user")
public class UserController {

   @Autowired
    private UserService userService;
   @Autowired
   private VolunteerService volunteerService;
   @Autowired
   private VolunteerMapper volunteerMapper;

   @RequestMapping("register")
   @ResponseBody
    public Result register( @RequestBody User user) {

        Result result = userService.register(user);
        return result;
    }
    @RequestMapping("login")
    @ResponseBody
    public Result login(
            @RequestParam("userName") String username, @RequestParam("userPassword") String password, HttpServletRequest request) {

       Result result = userService.login(username,password,request);
       return result;

    }

    //根据userId查询用户
    @GetMapping("{userId}")
    public Result getUserById(@PathVariable("userId") Integer userId){
        User result = userService.selectUserByUserId(userId);
        if (result != null){
            return Result.success(result);
        }else {
            return Result.error("未查询到该信息");
        }
    }
    @PatchMapping("updateUser")
    public Result updateUser( @RequestBody User  user){
        System.out.println(user);
      return   userService.updateUser(user);
    }

    //根据userId修改user_role
    @PatchMapping("updateUserByUserId")
    public Result updateUserByUserId(@RequestBody User user) {
        // 1. 根据用户ID查询对应的志愿者信息
        List<Volunteer> volunteers = volunteerService.selectVolunteerByUserId(user.getUserId());
        if (volunteers == null || volunteers.isEmpty()) {
            return Result.error("该用户未注册为志愿者");
        }
        Volunteer volunteer = volunteers.get(0); // 假设一个用户对应一个志愿者记录

        // 2. 更新志愿者状态（此处可根据业务需求修改volunteer的字段）
        int updateCount = volunteerMapper.updateVolunteer(volunteer);
        if (updateCount <= 0) {
            return Result.error("修改志愿者信息失败");
        }

        // 3. 若状态改为“已激活”，更新用户角色
        if ("已激活".equals(volunteer.getVolunteerStatus())) {
            user.setUserRole("volunteer");
            userService.updateById(user);
        }

        return Result.success("更新成功");
    }

    @PostMapping("addMoney")
    public Result userAddMoney(@RequestParam("userId") Integer userId,@RequestParam("amount") Integer amount) {
     return    userService.userAddMoney(userId,amount);
    }
}
