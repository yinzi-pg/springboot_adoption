package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.PetBoarding;
import com.ambow.springboot_adoption.vo.Result;
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



    @Select("SELECT \n" +
            "  user_pet_id AS userPetId,\n" +
            "  user_id AS userId,\n" +
            "  pet_name AS petName,\n" +
            "  pet_type AS petType,\n" +
            "  pet_age AS petAge,\n" +
            "  pet_gender AS petGender,\n" +
            "  health_status AS healthStatus,\n" +
            "  vaccine_status AS vaccineStatus,\n" +
            "  special_habit AS specialHabit,\n" +
            "  pet_image AS petImage,\n" +
            "  create_time AS createTime,\n" +
            "  pet_size AS petSize\n " +
            "FROM userpet_boarding \n" +
            "WHERE \n" +
            "  pet_name LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  pet_type LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  user_id LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  health_status LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  pet_gender LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  vaccine_status LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  pet_size LIKE CONCAT('%', #{keyword}, '%')\n")
    List<UserPetBoarding> searchUserPetBoarding(String keyword);

    @Select("select pet_image from userpet_boarding where user_pet_id = #{userPetId}")
    String getImageByUserPetId(Integer userPetId);
}
