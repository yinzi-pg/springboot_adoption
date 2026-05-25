package com.ambow.springboot_adoption.controller;

import com.ambow.springboot_adoption.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class UploadController {
    @RequestMapping("upload")
    @ResponseBody
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        Result result = new Result();
        String originalFilename = file.getOriginalFilename();
        String fileName   =   UUID.randomUUID().toString()+originalFilename.substring(originalFilename.lastIndexOf("."));
        file.transferTo(new File("D:/img_test/"+fileName));
        result.setCode(0);
        result.setMessage("上传成功");
        result.setData("http://localhost:8080/springboot_adoption/images/" + fileName);
        return result;
    }


}
