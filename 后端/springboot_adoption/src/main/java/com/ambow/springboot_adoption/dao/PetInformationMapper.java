package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.Pet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
