package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.service.PetInformationService;
import com.ambow.springboot_adoption.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/pet/")
@Controller
@ResponseBody
@CrossOrigin
public class PetInformationController {
     @Autowired
    private PetInformationService petInformationService;

    @GetMapping
    public Result getPetList(
           @RequestParam("pageNum") Integer pageNum,
           @RequestParam("pageSize")  Integer pageSize ) {
       return petInformationService.getPetList(pageNum, pageSize);

    }

     @GetMapping("search")
    public Result searchPetService(  @RequestParam("keyword")  String keyword,
                                     @RequestParam("pageNum") Integer pageNum,
                                     @RequestParam("pageSize")  Integer pageSize) {
         return petInformationService.searchPetService(keyword, pageNum, pageSize);
     }
}
