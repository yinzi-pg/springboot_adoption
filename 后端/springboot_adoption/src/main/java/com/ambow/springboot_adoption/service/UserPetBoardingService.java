package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.UserPetBoarding;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface UserPetBoardingService {

    IPage<UserPetBoarding> selectAllUserPetBoarding(int pageNum, int pageSize);

    int addUserPetBoarding(UserPetBoarding userPetBoarding);

    int updateUserPetBoarding(UserPetBoarding userPetBoarding);

    Integer deleteUserPetBoarding(Integer userPetBoarding);

    List<UserPetBoarding> selectUserPetBoardingByUserId(Integer userId);

    String selectPetSizeByUserPetId(Integer userPetId);
}
