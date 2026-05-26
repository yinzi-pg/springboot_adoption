package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.UserService;
import com.ambow.springboot_adoption.service.VolunteerService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.Volunteer;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteer/")
@CrossOrigin
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    // 分页查询全部
    @GetMapping
    public Result selectAllVolunteer(
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize) {
        IPage<Volunteer> page = volunteerService.selectAllVolunteerPage(pageNum, pageSize);
        PageBean<Volunteer> pageBean = new PageBean<>(page.getTotal(), page.getRecords());
        return Result.success(pageBean);
    }

     //新增
//    @PostMapping("addVolunteer")
//    public Result addVolunteer(@RequestBody Volunteer volunteer) {//用于将 HTTP 请求体（body）中的内容自动绑定到方法参数对象上。常见的使用场景是处理客户端提交的 JSON 或 XML 格式数据，Spring 会借助 HttpMessageConverter 将其转换为指定的 Java 对象（此处为 Volunteer 类型）。
//        int result = volunteerService.addVolunteer(volunteer);
//        if (result > 0) {
//            return Result.success(result);
//        } else {
//            return Result.error("新增志愿者信息失败");
//        }
//    }
    @PostMapping("addVolunteer")
    public Result addVolunteer(@RequestBody Volunteer volunteer) {
        // 直接调用 Service，Service 已经完成校验和业务处理，返回 Result
        return volunteerService.addVolunteer(volunteer);
    }

     //修改
    @PatchMapping("updateVolunteer")
    public Result updateVolunteer(@RequestBody Volunteer volunteer) {
//        int result = volunteerService.updateVolunteer(volunteer);
//        if (result > 0) {
//            return Result.success(result);
//        } else {
//            return Result.error("修改志愿者信息失败");
//        }
        return volunteerService.updateVolunteer(volunteer);

//        // 1. 更新志愿者状态（使用原有服务方法名）
//        int updateCount = volunteerService.updateVolunteer(volunteer);
//        if (updateCount <= 0) {
//            return Result.error("修改志愿者信息失败");
//        }
//
//        // 2. 若状态改为“已激活”，更新用户角色
//        if ("已激活".equals(volunteer.getVolunteerStatus())) {
//            User user = userService.getById(volunteer.getUserId());
//            if (user == null) {
//                return Result.error("关联用户不存在");
//            }
//            user.setUserRole("volunteer");
//            userService.updateById(user); // 确保UserService有updateById方法
//        }
//
//        return Result.success("更新成功");
    }

    // 删除
    @DeleteMapping("{volunteerId}")
    public Result deleteVolunteer(@PathVariable("volunteerId") Integer volunteerId) {
        int result = volunteerService.deleteVolunteer(volunteerId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("删除志愿者信息失败");
        }
    }

    // 模糊查询
    @GetMapping("searchVolunteer")
    public Result searchVolunteer(@RequestParam("keyword") String keyword) {
        List<Volunteer> result = volunteerService.findVolunteerByKeyword(keyword);
        if (result != null) {
            return Result.success(result);
        } else {
            return Result.error("未找到相关信息");
        }
    }

    // 根据user_id查询
    @GetMapping("byUserId/{userId}")
    public Result selectVolunteerByUserId(@PathVariable("userId") Integer userId) {
        List<Volunteer> result = volunteerService.selectVolunteerByUserId(userId);
        if (result != null) {
            return Result.success(result);
        } else {
            return Result.error("未查询到该用户的志愿者信息");
        }
    }
}
