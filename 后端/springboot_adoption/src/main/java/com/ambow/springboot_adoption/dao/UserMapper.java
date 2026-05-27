package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Insert("""
        insert into user(
            user_name, user_password, user_gender, user_age, user_phone, user_email, user_role, create_date, avatar, money
        ) values(
            #{userName}, #{userPassword}, #{userGender}, #{userAge}, #{userPhone}, #{userEmail}, #{userRole}, now(), #{avatar}, 0
        )
""")
    Integer insertUser(User user);
    @Select("SELECT * from user where user_name = #{userName}")
    User getUserByUserName(String userName);


    //根据userId查询用户
    @Select("select * from user where user_id = #{userId}")
    User getUserById(Integer userId);

    @Update("<script>" +
            "UPDATE user " +
            "<set>" +
            "<if test='userName != null'>user_name = #{userName},</if>" +
            "<if test='userPassword != null'>user_password = #{userPassword},</if>" +
            "<if test='userGender != null'>user_gender = #{userGender},</if>" +
            "<if test='userAge != null'>user_age = #{userAge},</if>" +
            "<if test='userPhone != null'>user_phone = #{userPhone},</if>" +
            "<if test='userEmail != null'>user_email = #{userEmail},</if>" +
            "<if test='userRole != null'>user_role = #{userRole},</if>" +
            "<if test='avatar != null'>avatar = #{avatar},</if>" +
            "<if test='money != null'>money = #{money},</if>" +
            "</set>" +
            "WHERE user_id = #{userId}" +
            "</script>")
    int updateUserById(User user);
}
