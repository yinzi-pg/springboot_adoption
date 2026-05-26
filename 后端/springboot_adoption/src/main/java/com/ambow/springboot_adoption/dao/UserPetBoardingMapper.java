package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.PetBoarding;
import com.ambow.springboot_adoption.vo.UserPetBoarding;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserPetBoardingMapper {

    @Select("")
    IPage<UserPetBoarding> selectAllUserPetBoarding(IPage<PetBoarding> page);
}
