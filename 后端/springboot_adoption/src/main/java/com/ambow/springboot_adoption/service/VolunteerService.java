package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.PublicInformation;
import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.User;
import com.ambow.springboot_adoption.vo.Volunteer;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface VolunteerService {

    // 分页查询全部
//    IPage<Volunteer> selectAllVolunteerPage(int pageNum, int pageSize);
    Result selectAllVolunteerPage(int pageNum, int pageSize);

    // 新增志愿者
//    int addVolunteer(Volunteer volunteer);

    Result addVolunteer(Volunteer volunteer);

    // 修改志愿者
    Result updateVolunteer(Volunteer volunteer);

    // 删除志愿者
    int deleteVolunteer(Integer volunteerId);

    // 模糊查询
    IPage<Volunteer> findVolunteerByKeyword(String keyword,int pageNum, int pageSize);



    // 根据user_id查询
//    List<Volunteer> selectVolunteerByUserId(Integer userId);
    List<Volunteer> selectVolunteerByUserId(Integer userId);

}
