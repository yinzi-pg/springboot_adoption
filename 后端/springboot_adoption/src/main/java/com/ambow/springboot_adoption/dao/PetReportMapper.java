package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.PetReport;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PetReportMapper {

    //查全部
    @Select("select * from pet_report")
    //List<PetReport> selectAllPetReport();
    IPage<PetReport> selectAllPetReport(IPage<PetReport> page);

    //添加
    @Insert("insert into pet_report(pet_name,pet_type,pet_gender,pet_age," +
            "find_location,contact_info,report_desc,create_date,report_image) values (#{petName},#{petType},#{petGender}" +
            ",#{petAge},#{findLocation},#{contactInfo},#{reportDesc},now(),#{reportImage})")
    int addPetReport(PetReport petReport);
    //删除
    @Delete("delete from pet_report where report_id = #{reportId}")
    int deletePetReport(Integer reportId);
    //修改
    @Update("update pet_report set pet_name = #{petName}," +
            "pet_type = #{petType},pet_gender = #{petGender},pet_age = #{petAge}," +
            "find_location = #{findLocation},contact_info = #{contactInfo}," +
            "report_desc = #{reportDesc},report_image = #{reportImage}," +
            "create_date = now() where report_id = #{reportId}")
    int updatePetReport(PetReport petReport);
    //模糊查询
    @Select("select * from pet_report where " +
            "pet_name like concat('%',#{keyword},'%') or " +
            "pet_type like concat('%',#{keyword},'%') or "+
            "pet_gender like concat('%',#{keyword},'%') or "+
            "pet_age like concat('%',#{keyword},'%') or " +
            "find_location like concat('%',#{keyword},'%') or " +
            "contact_info like concat('%',#{keyword},'%') or " +
            "report_desc like concat('%',#{keyword},'%') ")
    List<PetReport> findPetReportByKeyword(String keyword);

}
