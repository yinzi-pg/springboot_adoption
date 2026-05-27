package com.ambow.springboot_adoption.service.impl;

import com.ambow.springboot_adoption.dao.UserMapper;
import com.ambow.springboot_adoption.dao.VolunteerMapper;
import com.ambow.springboot_adoption.service.VolunteerService;
import com.ambow.springboot_adoption.vo.PageBean;
import com.ambow.springboot_adoption.vo.User;
import com.ambow.springboot_adoption.vo.Volunteer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class VolunteerServiceImpl extends ServiceImpl<VolunteerMapper, Volunteer> implements VolunteerService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageBean<Volunteer> getVolunteerList(Integer userId, Integer pageNum, Integer pageSize) {
        Page<Volunteer> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        QueryWrapper<Volunteer> queryWrapper = new QueryWrapper<>();
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        Page<Volunteer> resultPage = this.page(page, queryWrapper);
        return new PageBean<>(resultPage.getTotal(), resultPage.getRecords());
    }

    @Override
    public Volunteer selectVolunteerByUserId(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID不能为空");
        }
        QueryWrapper<Volunteer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public void addVolunteer(Volunteer volunteer) {
        if (volunteer == null) {
            throw new IllegalArgumentException("志愿信息不能为空");
        }
        if (volunteer.getServiceHours() != null && volunteer.getServiceHours().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("服务时长不能为负数");
        }
        volunteer.setCreateTime(LocalDateTime.now());
        if (volunteer.getVolunteerStatus() == null) {
            volunteer.setVolunteerStatus("待认证");
        }
        this.save(volunteer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVolunteer(Volunteer volunteer) {
        if (volunteer == null || volunteer.getVolunteerId() == null) {
            throw new IllegalArgumentException("志愿信息或ID不能为空");
        }
        if (volunteer.getServiceHours() != null && volunteer.getServiceHours().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("服务时长不能为负数");
        }
        
        // 校验修改前和修改后的状态
        if ("已激活".equals(volunteer.getVolunteerStatus()) && volunteer.getUserId() != null) {
            User user = userMapper.getUserById(volunteer.getUserId());
            if (user != null && !"volunteer".equals(user.getUserRole())) {
                user.setUserRole("volunteer");
                userMapper.updateUserById(user);
            }
        }
        this.updateById(volunteer);
    }

    @Override
    public void deleteVolunteer(Integer volunteerId) {
        if (volunteerId == null) {
            throw new IllegalArgumentException("志愿者ID不能为空");
        }
        this.removeById(volunteerId);
    }

    @Override
    public List<Volunteer> searchVolunteerService(String keyword) {
        QueryWrapper<Volunteer> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like("volunteer_type", keyword)
                        .or().like("volunteer_status", keyword)
                        .or().like("remark", keyword);
        }
        return this.list(queryWrapper);
    }
}
