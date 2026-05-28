package com.ambow.springboot_adoption.service.Impl;

import com.ambow.springboot_adoption.dao.UserPetBoardingMapper;
import com.ambow.springboot_adoption.service.UserPetBoardingService;
import com.ambow.springboot_adoption.vo.PetBoarding;
import com.ambow.springboot_adoption.vo.UserPetBoarding;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPetBoardingServiceImpl implements UserPetBoardingService {
    @Autowired
    private UserPetBoardingMapper userPetBoardingMapper;
    @Override
    public IPage<UserPetBoarding> selectAllUserPetBoarding(int pageNum, int pageSize) {
        IPage<PetBoarding> page = new Page<>(pageNum,pageSize);
        return userPetBoardingMapper.selectAllUserPetBoarding(page);
    }

    @Override
    public int addUserPetBoarding(UserPetBoarding userPetBoarding) {
        return userPetBoardingMapper.addUserPetBoarding(userPetBoarding);
    }

    @Override
    public int updateUserPetBoarding(UserPetBoarding userPetBoarding) {
        return userPetBoardingMapper.updateUserPetBoarding(userPetBoarding);
    }

    @Override
    public Integer deleteUserPetBoarding(Integer userPetId) {
        return userPetBoardingMapper.deleteUserPetBoarding(userPetId);
    }

    @Override
    public List<UserPetBoarding> selectUserPetBoardingByUserId(Integer userId) {
        return userPetBoardingMapper.selectUserPetBoardingByUserId(userId);
    }

    @Override
    public String selectPetSizeByUserPetId(Integer userPetId) {
        return userPetBoardingMapper.selectPetSizeByUserPetId(userPetId);
    }
}
