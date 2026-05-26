package com.ambow.springboot_adoption.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ambow.springboot_adoption.service.PetBoardingService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.PetBoarding;
import com.ambow.springboot_adoption.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@CrossOrigin
@RequestMapping("/petBoarding/")
public class PetBoardingController {
    @Autowired
    private PetBoardingService petBoardingService;

//    查询所有
    @GetMapping
    public Result selectAllPetBoarding(@RequestParam("pageNum") int pageNum ,
                                           @RequestParam("pageSize") int pageSize){
        Result result = new Result<>();
        IPage<PetBoarding> page = petBoardingService.selectAllPetBoarding(pageNum,pageSize);
        PageBean<PetBoarding> pageBean = new PageBean<>(page.getTotal(),page.getRecords());
        return result.success(pageBean);
    }
//添加
    @GetMapping("/addPetBoarding/")
    public Result addPetBoarding(@RequestParam("pageNum") int pageNum ,
                                 @RequestParam("pageSize") int pageSize){
        Result result = new Result<>();
        IPage<PetBoarding> page = petBoardingService.addPetBoarding(pageNum,pageSize);
        PageBean<PetBoarding> pageBean = new PageBean<>(page.getTotal(),page.getRecords());
        return result.success(pageBean);
    }

    //删除

}
