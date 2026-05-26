package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.Volunteer;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface VolunteerService {

    // 分页查询全部
    IPage<Volunteer> selectAllVolunteerPage(int pageNum, int pageSize);

    // 新增志愿者
//    int addVolunteer(Volunteer volunteer);

    Result addVolunteer(Volunteer volunteer);

    // 修改志愿者
    int updateVolunteer(Volunteer volunteer);

    // 删除志愿者
    int deleteVolunteer(Integer volunteerId);

    // 模糊查询
    List<Volunteer> findVolunteerByKeyword(String keyword);

    // 根据user_id查询
    List<Volunteer> selectVolunteerByUserId(Integer userId);

}
