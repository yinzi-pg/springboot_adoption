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

    // 新增根据ID更新用户的方法（如果现有updateUser不满足需求）
    @Update("UPDATE user \n" +
            "    SET user_role = #{userRole}\n" +
            "    WHERE user_id = #{userId}")
    int updateUserById(User user);
}
