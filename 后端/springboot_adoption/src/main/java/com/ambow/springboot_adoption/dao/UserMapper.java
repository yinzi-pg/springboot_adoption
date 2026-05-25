package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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


}
