package com.ambow.springboot_adoption.service.Impl;

import com.ambow.springboot_adoption.dao.UserMapper;
import com.ambow.springboot_adoption.dao.VolunteerMapper;
import com.ambow.springboot_adoption.service.VolunteerService;
import com.ambow.springboot_adoption.vo.Result;
import com.ambow.springboot_adoption.vo.User;
import com.ambow.springboot_adoption.vo.Volunteer;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    @Autowired
    private VolunteerMapper volunteerMapper;
    @Autowired
    private UserMapper userMapper;

    // 分页查询全部
    @Override
    public IPage<Volunteer> selectAllVolunteerPage(int pageNum, int pageSize) {
        Page<Volunteer> page = new Page<>(pageNum, pageSize);
        return volunteerMapper.selectAllVolunteerPage(page);
    }

     //新增
//    @Override
//    public int addVolunteer(Volunteer volunteer) {
//        return volunteerMapper.addVolunteer(volunteer);
//    }


        @Override
        public Result addVolunteer(Volunteer volunteer) {
            // 1. 校验关联用户ID
            if (volunteer.getUserId() == null) {
                return Result.error("关联用户ID不能为空");
            }
            User user=userMapper.getUserById(volunteer.getUserId());
            if (user==null) {
                return Result.error("关联用户查询不到");
            }
            // 2. 校验服务类型
            if (volunteer.getVolunteerType() == null || volunteer.getVolunteerType().trim().isEmpty()) {
                return Result.error("服务类型不能为空");
            }
            // 3. 校验服务时长
            if (volunteer.getServiceHours() < 0) {
                return Result.error("服务时长必须大于或等于0");
            }
            // 4. 校验志愿者状态
            if (volunteer.getVolunteerStatus() == null || volunteer.getVolunteerStatus().trim().isEmpty()) {
                return Result.error("志愿者状态不能为空");
            }

            // 校验通过，执行插入操作
            int result = volunteerMapper.addVolunteer(volunteer);
            if (result > 0) {
                return Result.success(result);   // 成功时返回影响行数
            } else {
                return Result.error("新增志愿者信息失败");
            }
        }


    // 修改
    @Override
    public Result updateVolunteer(Volunteer volunteer) {
        if (volunteer.getUserId() == null) {
            return Result.error("关联用户ID不能为空");
        }
        User user=userMapper.getUserById(volunteer.getUserId());
        if (user==null) {
            return Result.error("关联用户查询不到");
        }
        if (volunteer.getServiceHours() < 0) {
            return Result.error("服务时长必须大于或等于0");
        }
        int update=volunteerMapper.updateVolunteer(volunteer);
        if (update > 0) {
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    // 删除
    @Override
    public int deleteVolunteer(Integer volunteerId) {
        return volunteerMapper.deleteVolunteer(volunteerId);
    }

    // 模糊查询
    @Override
    public List<Volunteer> findVolunteerByKeyword(String keyword) {
        return volunteerMapper.findVolunteerByKeyword(keyword);
    }

    // 根据user_id查询
    @Override
    public List<Volunteer> selectVolunteerByUserId(Integer userId) {
        return volunteerMapper.selectByUserId(userId);
    }
}
