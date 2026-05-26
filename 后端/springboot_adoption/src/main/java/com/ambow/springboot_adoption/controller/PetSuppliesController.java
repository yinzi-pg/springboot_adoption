package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.PetSuppliesService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.PetSupplies;
import com.ambow.springboot_adoption.vo.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin
@ResponseBody
@RequestMapping("/petSupplies/")
public class PetSuppliesController {
    @Autowired
    private PetSuppliesService petSuppliesService;

    @GetMapping
    public Result selectAllPetSuppliers(@Param("pageNum") int pageNum,
                                        @Param("pageSize") int pageSize){
        Result result = new Result<>();
        IPage<PetSupplies> page = petSuppliesService.selectAllPetSuppliers(pageNum,pageSize);
        PageBean<PetSupplies> pageBean  = new PageBean<>(page.getTotal(),page.getRecords());
        return result.success(pageBean);
    }

}
