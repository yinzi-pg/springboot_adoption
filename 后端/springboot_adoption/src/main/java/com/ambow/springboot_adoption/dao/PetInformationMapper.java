package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.model.PetModel;
import com.ambow.springboot_adoption.vo.Pet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PetInformationMapper {

     @Select("select * from pet")
     IPage<Pet> getAllPet(IPage<Pet>  page);

     @Select("select * from pet " +
             " where pet_name like   concat('%',#{keyword},'%') " +
             "    or  pet_type like  concat('%',#{keyword},'%') "  +
             "    or  pet_age  like  concat('%',#{keyword},'%') " +
             "    or  pet_desc like  concat('%',#{keyword},'%') " +
             "    or  pet_gender like concat('%',#{keyword},'%')")

     IPage<Pet> searchPetMapper(IPage<Pet>  page,@Param("keyword") String keyword);

     @Insert("INSERT INTO  pet(pet_name, pet_type, pet_gender, pet_age, pet_status, pet_desc, pet_image, create_date) " +
             " VALUES (#{petName},#{petType},#{petGender},#{petAge},#{petStatus},#{petDesc},#{petImage},NOW()) ")
    Integer addPet(PetModel petModel);

     @Update("update pet set pet_name = #{petName} ,pet_type = #{petType},pet_gender = #{petGender},pet_age = #{petAge}, pet_status = #{petStatus},pet_desc = #{petDesc},pet_image = #{petImage}" +
             " where pet_id =#{petId}")
    Integer updatePet(PetModel petModel);

     @Delete("delete from pet where pet_id = #{petId}")
     Integer deletePet(Integer petId);
}
