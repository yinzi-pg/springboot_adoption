package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.VolunteerService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.Volunteer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @GetMapping("/")
    public Result<PageBean<Volunteer>> getVolunteerList(
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "1", required = false) Integer page, 
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(defaultValue = "1", required = false) Integer pageNum) {
        try {
            // Accommodate both "page" and "pageNum" parameters
            Integer actualPage = (page != null && page != 1) ? page : (pageNum != null ? pageNum : 1);
            return Result.success(volunteerService.getVolunteerList(userId, actualPage, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/byUserId/{userId}")
    public Result<Volunteer> selectVolunteerByUserId(@PathVariable Integer userId) {
        try {
            return Result.success(volunteerService.selectVolunteerByUserId(userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/addVolunteer")
    public Result addVolunteerService(@RequestBody Volunteer volunteerModel) {
        try {
            volunteerService.addVolunteer(volunteerModel);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{volunteerId}")
    public Result deleteVolunteerService(@PathVariable Integer volunteerId) {
        try {
            volunteerService.deleteVolunteer(volunteerId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PatchMapping("/updateVolunteer")
    public Result updateVolunteerService(@RequestBody Volunteer volunteerModel) {
        try {
            volunteerService.updateVolunteer(volunteerModel);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/searchVolunteer")
    public Result<List<Volunteer>> searchVolunteerService(@RequestParam String keyword) {
        try {
            return Result.success(volunteerService.searchVolunteerService(keyword));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
