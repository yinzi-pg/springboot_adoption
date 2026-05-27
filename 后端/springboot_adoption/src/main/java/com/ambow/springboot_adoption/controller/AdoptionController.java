package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.AdoptionService;
import com.ambow.springboot_adoption.vo.Adoption;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adoption/")
@CrossOrigin
public class AdoptionController {
        @Autowired
    private AdoptionService adoptionService;
        //查全部
    @GetMapping
    public Result selectAllAdoption(@RequestParam("pageNum") int pageNum,@RequestParam("pageSize") int pageSize){
        IPage<Adoption> page = adoptionService.selectAllAdoptionPage(pageNum,pageSize);
        PageBean<Adoption> pageBean = new PageBean<>(page.getPages(),page.getRecords());
        return Result.success(pageBean);

    }
    //增加
    @PostMapping("addAdoption")
    public Result addAdoption(@RequestBody Adoption adoption){
        int result = adoptionService.addAdoption(adoption);
        if(result>0){
            return Result.success(result);
        }else {
            return  Result.error("新增寄养信息失败");
        }

    }
    //删除
@DeleteMapping("{adoptionId}")
    public Result deleteAdoption(@PathVariable("adoptionId") Integer adoptionId ){
        int result = adoptionService.deleteAdoption(adoptionId);
        if(result>0){
            return Result.success(result);
        }else {
            return  Result.error("删除成功");
        }
}
  //修改
    @PatchMapping("updateAdoption")
    public Result updateAdoption(@RequestBody Adoption adoption){
        int result = adoptionService.updateAdoption(adoption);
        if(result>0){
            return Result.success(result);
        }else {
            return  Result.error("修改成功");
        }
    }
    //模糊查询
    @GetMapping("search")
    public Result searchAdoption(
            @RequestParam String keyword,
            @RequestParam int pageNum,
            @RequestParam int pageSize) {

        IPage<Adoption> page = adoptionService.findAdoptionByKeyword(pageNum, pageSize, keyword);
        PageBean<Adoption> pageBean = new PageBean<>(page.getPages(), page.getRecords());

        return Result.success(pageBean);
    }
}
