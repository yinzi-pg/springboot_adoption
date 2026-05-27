package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.UserPetBoardingService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.UserPetBoarding;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@CrossOrigin
@RequestMapping("/userPetBoarding/")
public class UserPetBoardingController {
    @Autowired
    private UserPetBoardingService userPetBoardingService;

//    查询全部
    @GetMapping
    public Result selectAllUserPetBoarding(@RequestParam("pageNum") int pageNum ,
                                           @RequestParam("pageSize") int pageSize){
        Result result = new Result<>();
        IPage<UserPetBoarding> page = userPetBoardingService.selectAllUserPetBoarding(pageNum,pageSize);
        PageBean<UserPetBoarding> pageBean = new PageBean<>(page.getTotal(),page.getRecords());
        return result.success(pageBean);
    }



}
