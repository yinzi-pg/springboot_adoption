package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ambow.springboot_adoption.vo.PetBoarding;

public interface PetBoardingService {
    //    查询所有记录
    IPage<PetBoarding> selectAllPetBoarding(int pageNum, int pageSize);

    Result addPetBoarding(PetBoarding petBoarding);

    Result updatePetBoarding(PetBoarding petBoarding);

    Result deletePetBoarding(PetBoarding petBoarding);
}
