package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.Pet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PetInformationMapper {

     @Select("select * from pet")
     IPage<Pet> getAllPet(IPage<Pet>  page);

     @Select("select * from pet")
     IPage<Pet> searchPetService(IPage<Pet>  page,String keyword);
}
