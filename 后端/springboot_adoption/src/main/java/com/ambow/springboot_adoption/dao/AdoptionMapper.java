package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.Adoption;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdoptionMapper extends BaseMapper<Adoption> {
    @Update("UPDATE pet SET pet_status = #{status} WHERE pet_id = #{petId}")
    int updatePetStatus(Integer petId, String status);
}
