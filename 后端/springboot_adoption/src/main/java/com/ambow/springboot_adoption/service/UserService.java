package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

     Result register(User user);

     Result login(String username, String password, HttpServletRequest request);

     User selectUserByUserId(Integer userId);

     int updateById(User user);//

     Result userAddMoney(Integer userId, Integer amount);

}
