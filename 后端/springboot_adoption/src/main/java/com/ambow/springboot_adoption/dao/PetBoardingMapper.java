package com.ambow.springboot_adoption.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ambow.springboot_adoption.vo.PetBoarding;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
    IPage<PetBoarding> addPetBoarding(IPage<PetBoarding> page);
}
