package com.ambow.springboot_adoption.service.Impl;

import com.ambow.springboot_adoption.dao.PetInformationMapper;
import com.ambow.springboot_adoption.model.PetModel;
import com.ambow.springboot_adoption.service.PetInformationService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.Pet;
import com.ambow.springboot_adoption.vo.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class PetInformationServiceImpl implements PetInformationService {
    @Autowired
    private PetInformationMapper petInformationMapper;

    @Override
    public Result getPetList(Integer pageNum, Integer pageSize) {
        Result result = new Result();
        Page<Pet> page = new Page<>(pageNum, pageSize);
        IPage<Pet> allPet = petInformationMapper.getAllPet(page);
         result.setCode(0);
         result.setMessage("查询到宠物信息");
        PageBean<Pet> pageBean = new PageBean<>();
        pageBean.setTotal(allPet.getTotal());
        pageBean.setItems(allPet.getRecords());
         result.setData(pageBean);
         return result;

    }

    @Override
    public Result searchPetService(String keyword,Integer pageNum, Integer pageSize) {
        Result result = new Result();
        Page<Pet> page = new Page<>(pageNum, pageSize);
        IPage<Pet> petIPage = petInformationMapper.searchPetMapper(page, keyword);
        if (petIPage.getTotal() ==0) {
            result.setCode(1);
            result.setMessage("查询失败");
        }
        result.setCode(0);
        result.setMessage("查询成功");
        PageBean<Pet> pageBean = new PageBean<>();
        pageBean.setTotal(petIPage.getTotal());
        pageBean.setItems(petIPage.getRecords());
        result.setData(pageBean);
        return result;
    }

    @Override
    public Result addPet(PetModel petModel) {
        Integer i = petInformationMapper.addPet(petModel);
        if (i != 1){
          return   Result.error("添加失败");

        }
        return Result.success();

    }

    @Override
    public Result updatePet(PetModel petModel) {
        Integer i = petInformationMapper.updatePet(petModel);
        if (i != 1){
            return Result.error("更新失败");

        }
        return Result.success();
    }

    @Override
    public Result deletePet(Integer petId) {
        Integer i = petInformationMapper.deletePet(petId);
        if (i != 1){
            return Result.error("删除失败");
        }
        return Result.success();
    }
}
