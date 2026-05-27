package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.PetSuppliesService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.PetSupplies;
import com.ambow.springboot_adoption.vo.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/petSupplies/")
@CrossOrigin
public class PetSuppliesController {

    @Autowired
    private PetSuppliesService petSuppliesService;
    //查全部
    @GetMapping
    public Result selectAllPetSupplies(@RequestParam("pageNum") int pageNum,
                                       @RequestParam("pageSize") int pageSize){
        IPage<PetSupplies> page = petSuppliesService.selectAllPetSuppliesPage(pageNum,pageSize);
        PageBean<PetSupplies> pageBean = new PageBean<>(page.getTotal(),page.getRecords());
        return Result.success(pageBean);
    }
    //根据id查询商品详情
    @GetMapping("selectSupplyBySupplyId/{supplyId}")
    public Result selectSupplyBySupplyId(@PathVariable("supplyId") Integer supplyId){
        PetSupplies result = petSuppliesService.getSupplyById(supplyId);
        if ( result != null ){
            return Result.success(result);
        }else {
            return Result.error("未查询到该订单详情");
        }
    }
    //添加
    @PostMapping("addSupplies")
    public Result addPetSupplies(@RequestBody PetSupplies petSupplies){
        int result = petSuppliesService.addPetSupplies(petSupplies);
        if (result > 0 ){
            return Result.success(result);
        }else {
            return Result.error("新增宠物用品失败");
        }
    }
    //修改
    @PatchMapping("updateSupplies")
    public Result updatePetSupplies(@RequestBody PetSupplies petSupplies){
        int result = petSuppliesService.updatePetSupplies(petSupplies);
        if (result > 0 ){
            return Result.success(result);
        }else {
            return Result.error("修改宠物用品失败");
        }
    }
    //删除
    @DeleteMapping("{supplyId}")
    public Result deletePetSupplies(@PathVariable("supplyId") Integer supplyId){
        int result = petSuppliesService.deletePetSupplies(supplyId);
        if (result > 0 ){
            return Result.success(result);
        }else {
            return Result.error("删除宠物用品失败");
        }
    }
    //模糊查询
    @GetMapping("searchSupplies")
    public Result searchSupplies(@RequestParam("keyword") String keyword){
        List<PetSupplies> result = petSuppliesService.findPetSuppliesByKeyword(keyword);
        if (result !=null){
            return Result.success(result);
        }else {
            return Result.error("未找到相关信息");
        }
    }

}
