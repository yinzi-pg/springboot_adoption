package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.PetBoarding;
import com.ambow.springboot_adoption.vo.UserPetBoarding;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserPetBoardingMapper {

    @Select("select * from userpet_boarding")
    IPage<UserPetBoarding> selectAllUserPetBoarding(IPage<PetBoarding> page);

    @Insert("INSERT INTO userpet_boarding (\n" +
            "  user_id, pet_name, pet_type, pet_age, \n" +
            "  pet_gender, health_status, vaccine_status, \n" +
            "  special_habit, pet_image,create_time,pet_size\n" +
            ") VALUES (\n" +
            "  #{userId}, #{petName}, #{petType}, #{petAge}, \n" +
            "  #{petGender}, #{healthStatus}, #{vaccineStatus}, \n" +
            "  #{specialHabit}, #{petImage},now(),#{petSize}\n" +
            ")")
    int addUserPetBoarding(UserPetBoarding userPetBoarding);

    @Update("UPDATE userpet_boarding SET \n" +
            "  user_id = #{userId},\n" +
            "  pet_name = #{petName},\n" +
            "  pet_type = #{petType},\n" +
            "  pet_age = #{petAge},\n" +
            "  pet_gender = #{petGender},\n" +
            "  health_status = #{healthStatus},\n" +
            "  vaccine_status = #{vaccineStatus},\n" +
            "  special_habit = #{specialHabit},\n" +
            "  pet_image = #{petImage},\n" +
            "  pet_size = #{petSize} " +
            " WHERE user_pet_id = #{userPetId}")
    int updateUserPetBoarding(UserPetBoarding userPetBoarding);

    @Delete("delete from userpet_boarding where user_pet_id = #{userPetId}")
    Integer deleteUserPetBoarding(Integer userPetId);

    @Select("select * from userpet_boarding where user_id = #{userId}")
    List<UserPetBoarding> selectUserPetBoardingByUserId(Integer userPetId);

    @Select("select pet_size from userpet_boarding where user_pet_id = #{userPetId}")
    String selectPetSizeByUserPetId(Integer userPetId);
}
