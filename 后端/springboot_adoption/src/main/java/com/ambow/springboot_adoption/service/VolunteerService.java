package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.Volunteer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface VolunteerService extends IService<Volunteer> {
    PageBean<Volunteer> getVolunteerList(Integer userId, Integer page, Integer pageSize);
    Volunteer selectVolunteerByUserId(Integer userId);
    void addVolunteer(Volunteer volunteer);
    void updateVolunteer(Volunteer volunteer);
    void deleteVolunteer(Integer volunteerId);
    List<Volunteer> searchVolunteerService(String keyword);
}
