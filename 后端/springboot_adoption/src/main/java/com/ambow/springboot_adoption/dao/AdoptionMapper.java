package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.Adoption;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdoptionMapper {
    // 查询全部
    @Select("select * from adoption")
      IPage<Adoption> selectAllAdoptionPage(IPage<Adoption> page);
    //添加
    @Insert("INSERT INTO adoption (\n" +
            "  applicant_name, applicant_phone, pet_id, apply_date, \n" +
            "  status, remark, review_date\n" +
            ") VALUES (\n" +
            "  #{applicantName}, #{applicantPhone}, #{petId}, now(), \n" +
            "  #{status}, #{remark}, now()\n" +
            ")")
      int addAdoption(Adoption adoption);
    //删除
    @Delete("delete from adoption where adoption_id = #{adoptionId}")
    int deleteAdoption(Integer adoptionId);
    //修改
    @Update("UPDATE adoption SET \n" +
            "  applicant_name = #{applicantName},\n" +
            "  applicant_phone = #{applicantPhone},\n" +
            "  pet_id = #{petId},\n" +
            "  apply_date = now(),\n" +
            "  status = #{status},\n" +
            "  remark = #{remark},\n" +
            "  review_date = now()\n" +
            "WHERE adoption_id = #{adoptionId}")
    int updateAdoption(Adoption adoption);
    //模糊查询
    @Select("SELECT \n" +
            "  adoption_id AS adoptionId,\n" +
            "  applicant_name AS applicantName,\n" +
            "  applicant_phone AS applicantPhone,\n" +
            "  pet_id AS petId,\n" +
            "  apply_date AS applyDate,\n" +
            "  status,\n" +
            "  remark,\n" +
            "  review_date AS reviewDate\n" +
            "FROM adoption \n" +
            "WHERE \n" +
            "  applicant_name LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  applicant_phone LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  status LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  pet_id LIKE CONCAT('%', #{keyword}, '%')")
    IPage<Adoption> findAdoptionByKeyword(Page<Adoption> page, String keyword);
}
