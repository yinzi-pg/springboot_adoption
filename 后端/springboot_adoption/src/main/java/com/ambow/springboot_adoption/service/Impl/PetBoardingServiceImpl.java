package com.ambow.springboot_adoption.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ambow.springboot_adoption.dao.PetBoardingMapper;
import com.ambow.springboot_adoption.service.PetBoardingService;
import com.ambow.springboot_adoption.vo.PetBoarding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public IPage<PetBoarding> addPetBoarding(int pageNum, int pageSize) {
        IPage<PetBoarding> page = new Page<>(pageNum,pageSize);
        return petBoardingMapper.addPetBoarding(page);
    }
}
