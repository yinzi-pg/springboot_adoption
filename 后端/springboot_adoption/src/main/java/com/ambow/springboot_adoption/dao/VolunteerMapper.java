package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.Volunteer;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VolunteerMapper {

    // 分页查询全部志愿者
    @Select("select * from volunteer")
    IPage<Volunteer> selectAllVolunteerPage(IPage<Volunteer> page);

    // 新增志愿者
    @Insert("insert into volunteer(" +
            "user_id, volunteer_type, service_hours, volunteer_status, create_time, remark,volunteer_image" +
            ") values(" +
            "#{userId}, #{volunteerType}, #{serviceHours}, #{volunteerStatus}, now(), #{remark},#{volunteerImage}" +
            ")")
    int addVolunteer(Volunteer volunteer);

    // 修改志愿者信息
    @Update("update volunteer set " +
            "user_id = #{userId}, " +
            "volunteer_type = #{volunteerType}, " +
            "service_hours = #{serviceHours}, " +
            "volunteer_status = #{volunteerStatus}, " +
            "remark = #{remark}," +
            "volunteer_image = #{volunteerImage} " +
            "where volunteer_id = #{volunteerId}")
    int updateVolunteer(Volunteer volunteer);

    // 删除志愿者
    @Delete("delete from volunteer where volunteer_id = #{volunteerId}")
    int deleteVolunteer(Integer volunteerId);

    // 模糊查询（按服务类型、状态、备注）
    @Select("select * from volunteer where " +
            "volunteer_type like concat('%', #{keyword}, '%') or " +
            "volunteer_status like concat('%', #{keyword}, '%') or " +
            "remark like concat('%', #{keyword}, '%') or " +
            "user_id like concat('%', #{keyword}, '%')")
    IPage<Volunteer> findVolunteerByKeyword( @Param("keyword") String keyword,IPage<Volunteer> page);
    // 根据user_id查询志愿者信息
    @Select("select * from volunteer where user_id = #{userId}")
    List<Volunteer> selectByUserId(Integer userId);
}
