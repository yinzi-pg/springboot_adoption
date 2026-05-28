package com.ambow.springboot_adoption.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ambow.springboot_adoption.vo.PetBoarding;
import org.apache.ibatis.annotations.*;
import com.ambow.springboot_adoption.vo.Result;

@Mapper
public interface PetBoardingMapper {

    @Select("select pb.* ,upb.pet_name as petName , upb.pet_image as petImage , u.user_name as userName , u.user_phone as userPhone " +
            " from pet_boarding pb " +
            " left join userpet_boarding upb ON pb.user_pet_id = upb.user_pet_id " +
            " left join user u ON pb.user_id = u.user_id ")
    IPage<PetBoarding> selectAllPetBoarding(@Param("page") IPage<PetBoarding> page);

    @Insert("insert into pet_boarding(user_pet_id,pet_image,user_id,boarding_start," +
            "boarding_end,boarding_status,fee,special_instructions,payment_status,create_time) values(" +
            "#{userPetId},#{petImage},#{userId},#{boardingStart},#{boardingEnd},#{boardingStatus},#{fee},#{specialInstructions}," +
            "#{paymentStatus},now()) ")
    int addPetBoarding(PetBoarding petBoarding);

    @Update("update pet_boarding set user_pet_id = #{userPetId},pet_image = #{petImage},user_id = #{userId}," +
            "boarding_start = #{boardingStart},boarding_end = #{boardingEnd},boarding_status = #{boardingStatus},fee = #{fee}," +
            "special_instructions = #{specialInstructions},payment_status = #{paymentStatus},create_time = now()" +
            "where boarding_id = #{boardingId}")
    int updatePetBoarding(PetBoarding petBoarding);

    @Delete("delete from pet_boarding where boarding_id = #{boardingId}")
    int deletePetBoarding(PetBoarding petBoarding);
}
