package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.UserPetBoarding;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface UserPetBoardingService {

    IPage<UserPetBoarding> selectAllUserPetBoarding(int pageNum, int pageSize);
}
