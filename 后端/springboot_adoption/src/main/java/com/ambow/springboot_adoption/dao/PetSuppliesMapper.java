package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.PetSupplies;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PetSuppliesMapper {
    @Select("select * from pet_supplies")
    IPage<PetSupplies> selectAllPetSupplies(IPage<PetSupplies> page);
}
