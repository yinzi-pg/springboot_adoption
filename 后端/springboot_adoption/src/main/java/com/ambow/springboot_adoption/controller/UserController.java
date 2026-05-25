package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.UserService;
import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


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

@Controller
@RequestMapping("user")
public class UserController {

   @Autowired
    private UserService userService;

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
}
