package com.ambow.springboot_adoption.service.Impl;

import com.ambow.springboot_adoption.dao.UserMapper;
import com.ambow.springboot_adoption.service.UserService;
import com.ambow.springboot_adoption.utils.Md5Util;
import com.ambow.springboot_adoption.utils.PhoneUtil;
import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.User;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result register(User user) {
//        调用参数校验方法
       Result result = parameterValidation(user);
       if (result.getCode()!=0){
           return result;
       }
//       密码Md5加密
        String encode = Md5Util.encode(user.getUserPassword());
        user.setUserPassword(encode);


//      调用dao层添加方法
        Integer i = userMapper.insertUser(user);
       if (i == 1){
           result.setCode(0);
           result.setMessage("注册成功");
       }else {
           result.setCode(1);
           result.setMessage("注册失败");
       }
        return result;
    }

    @Override
    public Result login(String username, String password, HttpServletRequest request) {
        Result result = loginParameterValidation(username, password);
        if (result.getCode()!=0){
            return result;
        }
        User user = userMapper.getUserByUserName(username);
        if (user==null){
            result.setCode(1);
            result.setMessage("用户不存在");
            return result;
        }
        if (!user.getUserPassword().equals(Md5Util.encode(password))){
            result.setCode(1);
            result.setMessage("密码不正确");
            return result;
        }
        result.setCode(0);
        result.setMessage("登录成功");
        result.setData(user);
        request.setAttribute("user", user);
        return result;
    }

    private Result loginParameterValidation(String username, String password) {
      Result result = new Result();
        if (StringUtils.isBlank(username)) {
            result.setCode(1);
            result.setMessage("名字不能为空");
            return result;
        }
        if (StringUtils.isBlank(password)) {
            result.setCode(1);
            result.setMessage("密码不能为空");
            return result;
        }
        result.setCode(0);
      return   result;

    }


    //    参数校验
    private Result parameterValidation(User user) {
        Result result = new Result();
          if (StringUtils.isBlank(user.getUserName())){
              result.setCode(1);
              result.setMessage("名字不能为空");
              return result;
          }
          if (userMapper.getUserByUserName(user.getUserName())!=null){
              result.setCode(1);
              result.setMessage("名字被占用");
              return result;
          }
          if (StringUtils.isBlank(user.getUserPassword())){
              result.setCode(1);
              result.setMessage("密码不能为空");
              return result;
          }
          if (StringUtils.isBlank(user.getUserRole())){
              result.setCode(1);
              result.setMessage("角色不能为空");
              return result;
          }
        if (StringUtils.isBlank(user.getUserPhone())){
            result.setCode(1);
            result.setMessage("手机号不能为空");
            return result;
        }
        if (!PhoneUtil.isMobile(user.getUserPhone())){
            result.setCode(1);
            result.setMessage("手机号不合法");
            return result;
        }

         result.setCode(0);

          return result;


    }
}
