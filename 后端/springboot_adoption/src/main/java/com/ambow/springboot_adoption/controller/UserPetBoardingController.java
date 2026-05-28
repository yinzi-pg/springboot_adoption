package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.UserPetBoardingService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.UserPetBoarding;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@CrossOrigin
@RequestMapping("/userPetBoarding/")
public class UserPetBoardingController {
    @Autowired
    private UserPetBoardingService userPetBoardingService;

    //    查询全部
    @GetMapping
    public Result selectAllUserPetBoarding(@RequestParam("pageNum") int pageNum,
                                           @RequestParam("pageSize") int pageSize) {
        Result result = new Result<>();
        IPage<UserPetBoarding> page = userPetBoardingService.selectAllUserPetBoarding(pageNum, pageSize);
        PageBean<UserPetBoarding> pageBean = new PageBean<>(page.getTotal(), page.getRecords());
        return result.success(pageBean);
    }

    @PostMapping("addUserPetBoarding")
    public Result addUserPetBoarding(@RequestBody UserPetBoarding userPetBoarding) {
        Integer i = userPetBoardingService.addUserPetBoarding(userPetBoarding);
        if (i != 1) {
            return Result.error("添加失败");
        }
        return Result.success();
    }


    @PatchMapping("updateUserPetBoarding")
    public Result updateUserPetBoarding(@RequestBody UserPetBoarding userPetBoarding) {
        Integer i = userPetBoardingService.updateUserPetBoarding(userPetBoarding);
        if (i != 1) {
            return Result.error("更新失败");
        }
        return Result.success();
    }

    @DeleteMapping("{userPetId}")
    public Result deleteUserPetBoarding(@PathVariable("userPetId") Integer userPetId) {
        Integer i = userPetBoardingService.deleteUserPetBoarding(userPetId);
        if (i != 1) {
            return Result.error("删除失败");
        }
        return Result.success();
    }

//    模糊查询
    @GetMapping("searchUserPetBoarding")
    public Result searchUserPetBoarding(@RequestParam("keyword") String keyword){
        return userPetBoardingService.searchUserPetBoarding(keyword);
    }

    @GetMapping("GetImageByUserPetId/{userPetId}")
    public Result getImageByUserPetId(@PathVariable("userPetId") Integer userPetId){
        return userPetBoardingService.getImageByUserPetId(userPetId);
    }

//    根据userId查询用户宠物寄养信息
    @GetMapping("byUserId/{userId}")
    public Result selectUserPetBoardingByUserId(@PathVariable("userId") Integer userId){
        List<UserPetBoarding> i = userPetBoardingService.selectUserPetBoardingByUserId(userId);
        if (i != null){
            return Result.success();
        }
        return Result.error("未获取到信息");
    }

//    根据user_pet_id查询petSize
    @GetMapping("byUserPetId/{userPetId}")
    public Result selectPetSizeByUserPetId(@PathVariable("userPetId") Integer userPetId){
        String i = userPetBoardingService.selectPetSizeByUserPetId(userPetId);
        if (i != null){
            return Result.success();
        }
        return Result.error("未获取到信息");
    }
}
