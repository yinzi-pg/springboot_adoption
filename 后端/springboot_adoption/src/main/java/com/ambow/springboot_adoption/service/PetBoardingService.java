package com.ambow.springboot_adoption.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ambow.springboot_adoption.vo.PetBoarding;

public interface PetBoardingService {
////    查询所有记录
//
////    添加，返回变化行数
//    int addPetBoarding(PetBoarding petBoarding);
////    修改，返回变化行数
//    int updatePetBoarding(PetBoarding petBoarding);
////    删除，返回变化行数
//    int deletePetBoarding(PetBoarding petBoarding);
////    模糊搜索，返回list集合
//    List<PetBoarding> seletePetBoardingByKeywoed();

    //    查询所有记录
    IPage<PetBoarding> selectAllPetBoarding(int pageNum, int pageSize);

    IPage<PetBoarding> addPetBoarding(int pageNum, int pageSize);
}
