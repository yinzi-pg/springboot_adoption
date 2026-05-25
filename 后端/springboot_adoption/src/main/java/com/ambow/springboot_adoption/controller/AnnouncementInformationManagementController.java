package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.Impl.CZServiceImpl;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.PublicInformation;
import com.ambow.springboot_adoption.vo.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/")
@CrossOrigin
public class AnnouncementInformationManagementController {
    @Autowired
    private CZServiceImpl czService;

    //查全部（有分页）
    @GetMapping
    public Result selectAllPublicInformation(@RequestParam("pageNum") int pageNum,
                                             @RequestParam("pageSize") int pageSize) {
        IPage<PublicInformation> page = czService.selectAllPublicInformationPage(pageNum,pageSize);
        PageBean<PublicInformation> pageBean = new PageBean<>(page.getTotal(),page.getRecords());
        return Result.success(pageBean);
    }
    //添加
    @PostMapping("addPublic")
    public Result addPublic(@RequestBody PublicInformation publicInformation) {
        int result = czService.addPublicInformation(publicInformation);
        if (result > 0 ){
            return Result.success();
        }else {
            return Result.error("新增公告信息失败");
        }
    }
    //修改
    @PatchMapping("updatePublic")
    public Result updatePublic(@RequestBody PublicInformation publicInformation) {
        int result = czService.updatePublicInformation(publicInformation);
        if (result > 0 ){
            return Result.success();
        }else {
            return Result.error("修改公告信息失败");
        }
    }
    //删除
    @DeleteMapping("{publicId}")
    public Result deletePublic(@PathVariable("publicId") Integer publicId){
        int result = czService.deletePublicInformation(publicId);
        if (result > 0 ){
            return Result.success();
        }else {
            return Result.error("删除公告信息失败");
        }
    }
    //模糊查询
    @GetMapping("searchPublic")
    public Result searchPublic(@RequestParam("keyword") String keyword) {
        List<PublicInformation> result = czService.findPublicByKeyword(keyword);
        if ( result !=null){
            return Result.success();
        }else {
            return Result.error("未找到相关信息");
        }
    }
}
