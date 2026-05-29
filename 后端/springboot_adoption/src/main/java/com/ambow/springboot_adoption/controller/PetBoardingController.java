package com.ambow.springboot_adoption.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ambow.springboot_adoption.service.PetBoardingService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.PetBoarding;
import com.ambow.springboot_adoption.vo.Result;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("addPetBoarding")
    public Result addPetBoarding(@RequestBody PetBoarding petBoarding){
        return petBoardingService.addPetBoarding(petBoarding);
    }

//    修改
    @PatchMapping ("updatePetBoarding")
    public Result updatePetBoarding(@RequestBody PetBoarding petBoarding){
        return petBoardingService.updatePetBoarding(petBoarding);

}
    //删除
    @DeleteMapping("{boardingId}")
    public Result deletePetBoarding(@PathVariable Integer boardingId){
        return petBoardingService.deletePetBoarding(boardingId);
    }

    //模糊查询
    @GetMapping("searchPetBoarding")
    public Result searchUserPetBoarding(@RequestParam("keyword") String keyword){
        return petBoardingService.searchUserPetBoarding(keyword);
    }

}
