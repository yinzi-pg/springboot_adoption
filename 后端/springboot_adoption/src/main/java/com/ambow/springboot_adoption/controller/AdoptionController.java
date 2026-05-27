package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.AdoptionService;
import com.ambow.springboot_adoption.vo.Adoption;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adoption")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @GetMapping("/")
    public Result<PageBean<Adoption>> getAdoptionList(
            @RequestParam(defaultValue = "1", required = false) Integer page, 
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(defaultValue = "1", required = false) Integer pageNum) {
        try {
            Integer actualPage = (page != null && page != 1) ? page : (pageNum != null ? pageNum : 1);
            return Result.success(adoptionService.getAdoptionList(actualPage, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/addAdoption")
    public Result addAdoptionService(@RequestBody Adoption adoptionModel) {
        try {
            adoptionService.addAdoption(adoptionModel);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PatchMapping("/updateAdoption")
    public Result updateAdoptionService(@RequestBody Adoption adoptionModel) {
        try {
            adoptionService.updateAdoption(adoptionModel);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{adoptionId}")
    public Result deleteAdoptionService(@PathVariable Integer adoptionId) {
        try {
            adoptionService.deleteAdoption(adoptionId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/search")
    public Result<PageBean<Adoption>> searchAdoptionService(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1", required = false) Integer page, 
            @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(defaultValue = "1", required = false) Integer pageNum) {
        try {
            Integer actualPage = (page != null && page != 1) ? page : (pageNum != null ? pageNum : 1);
            return Result.success(adoptionService.searchAdoptionService(keyword, actualPage, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
