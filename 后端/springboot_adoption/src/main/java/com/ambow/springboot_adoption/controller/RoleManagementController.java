package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.Impl.CZServiceImpl;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.Role;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role/")
@CrossOrigin
public class RoleManagementController {
    @Autowired
    private CZServiceImpl czServiceImpl;

    //查全部
    @GetMapping
    public Result selectAllRoles(@RequestParam("pageNum") int pageNum,
                                 @RequestParam("pageSize") int pageSize){
        IPage<Role> page = czServiceImpl.selectAllRolePage(pageNum,pageSize);
        PageBean<Role> pageBean = new PageBean<>(page.getTotal(),page.getRecords());
        return Result.success(pageBean);
    }
    //添加
    @PostMapping("addRole")
    public Result addRole(@RequestBody Role role){
        int result = czServiceImpl.addRole(role);
        if (result > 0 ){
            return Result.success(result);
        }else {
            return Result.error("新增角色失败");
        }
    }
    //修改
    @PatchMapping("updateRole")
    public Result updateRole(@RequestBody Role role){
        int result = czServiceImpl.updateRole(role);
        if (result > 0 ){
            return Result.success(result);
        }else {
            return Result.error("修改角色失败");
        }
    }
    //删除
    @DeleteMapping("{roleId}")
    public Result deleteRole(@PathVariable("roleId") Integer roleId){
        int result = czServiceImpl.deleteRole(roleId);
        if (result > 0 ){
            return Result.success(result);
        }else {
            return Result.error("删除角色失败");
        }
    }
    //模糊查询
    @GetMapping("searchRole")
    public Result searchRole(@RequestParam("keyword") String keyword){
        List<Role> result = czServiceImpl.findRoleByKeyword(keyword);
        if (result != null){
            return Result.success(result);
        }else{
            return Result.error("未找到相关信息");
        }
    }
}
