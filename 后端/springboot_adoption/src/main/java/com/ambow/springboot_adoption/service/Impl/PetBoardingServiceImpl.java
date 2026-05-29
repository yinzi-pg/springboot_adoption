package com.ambow.springboot_adoption.service.Impl;

import com.ambow.springboot_adoption.dao.UserPetBoardingMapper;
import com.ambow.springboot_adoption.vo.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ambow.springboot_adoption.dao.PetBoardingMapper;
import com.ambow.springboot_adoption.service.PetBoardingService;
import com.ambow.springboot_adoption.vo.PetBoarding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetBoardingServiceImpl implements PetBoardingService {
    @Autowired
    private PetBoardingMapper petBoardingMapper;

    @Override
    public IPage<PetBoarding> selectAllPetBoarding(int pageNum, int pageSize) {
        IPage<PetBoarding> page = new Page<>(pageNum,pageSize);
        IPage<PetBoarding> petBoardingIPage = petBoardingMapper.selectAllPetBoarding(page);
        return petBoardingIPage;
    }

    @Override
    public Result addPetBoarding(PetBoarding petBoarding) {
        Integer i = petBoardingMapper.addPetBoarding(petBoarding);
        if (i != 1){
            return Result.error("添加失败");
        }
        return Result.success();
    }

    @Override
    public Result updatePetBoarding(PetBoarding petBoarding) {
        Integer i = petBoardingMapper.updatePetBoarding(petBoarding);
        if (i != 1){
            return Result.error("更新失败");
        }
        return Result.success();
    }

    @Override
    public Result deletePetBoarding(Integer boardingId) {
        Integer i = petBoardingMapper.deletePetBoarding(boardingId);
        if (i != 1){
            return Result.error("删除失败");
        }
        return Result.success();
    }

    @Override
    public Result searchUserPetBoarding(String keyword) {
        List<PetBoarding> i = petBoardingMapper.searchUserPetBoarding(keyword);
        if (i != null){
            return Result.success(i);
        }
        return Result.error("未查询到相关信息");
    }


}
