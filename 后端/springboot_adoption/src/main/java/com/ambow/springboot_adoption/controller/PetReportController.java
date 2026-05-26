package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.PetReportService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.PetReport;
import com.ambow.springboot_adoption.vo.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/petReport/")
@CrossOrigin
public class PetReportController {

    @Autowired
    private PetReportService petReportService;

    //查全部
   @GetMapping
    public Result selectAllPetReport(@RequestParam("pageNum") int pageNum,
                                     @RequestParam("pageSize") int pageSize){
       IPage<PetReport> page = petReportService.selectAllPetReportPage(pageNum,pageSize);
       PageBean<PetReport> pageBean = new PageBean<>(page.getTotal(),page.getRecords());
       return Result.success(pageBean);
   }
    //添加
    @PostMapping("addPetReport")
    public Result addPetReport(@RequestBody PetReport petReport){
       int result = petReportService.addPetReport(petReport);
       if (result > 0 ){
           return Result.success(result);
       }else {
           return Result.error("新增流浪宠物信息失败");
       }
    }
    //修改
    @PatchMapping("updatePetReport")
    public Result updatePetReport(@RequestBody PetReport petReport){
       int result = petReportService.updatePetReport(petReport);
       if (result > 0 ){
           return Result.success(result);
       }else {
           return Result.error("修改流浪宠物信息失败");
       }
    }
    //删除
    @DeleteMapping("{reportId}")
    public Result deletePetReport(@PathVariable("reportId") Integer reportId){
       int result = petReportService.deletePetReport(reportId);
       if (result > 0 ){
           return Result.success(result);
       }else{
           return Result.error("删除流浪宠物信息失败");
       }
    }
    //模糊查询
    @GetMapping("searchPetReport")
    public Result searchPetReport(@RequestParam("keyword")String keyword){
       List<PetReport> result = petReportService.findPetReportByKeyword(keyword);
       if (result != null){
           return Result.success(result);
       }else {
           return Result.error("未找到相关信息");
       }
    }

}
