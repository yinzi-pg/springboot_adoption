package com.ambow.springboot_adoption.dao;

import com.ambow.springboot_adoption.vo.OrderItem;
import com.ambow.springboot_adoption.vo.PetSupplies;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Mapper
public interface PetSuppliesMapper {

    //查全部
    @Select("select * from pet_supplies")
    //List<PetSupplies> selectAllPetSupplies();
    IPage<PetSupplies> selectAllPetSupplies(IPage<PetSupplies> page);
    //根据id查询商品
    @Select("select * from pet_supplies where supply_id = #{supplyId}")
    PetSupplies getSupplyById(Integer supplyId);
    //添加
    @Insert("insert into pet_supplies(supply_name,supply_type,brand,price,stock,status," +
            "description,supply_image,create_date) values (#{supplyName},#{supplyType}," +
            "#{brand},#{price},#{stock},#{status},#{description},#{supplyImage},now())")
    int addPetSupplies(PetSupplies petSupplies);
    //修改
    @Update("update pet_supplies set supply_name =#{supplyName}, supply_type = #{supplyType}," +
            "brand = #{brand},price = #{price},stock = #{stock},status = #{status}," +
            "description = #{description},supply_image = #{supplyImage},create_date = now() where " +
            "supply_id = #{supplyId}")
    int updatePetSupplies(PetSupplies petSupplies);
    //删除
    @Delete("delete from pet_supplies where supply_id = #{supplyId}")
    int deletePetSupplies(Integer supplyId);
    //模糊查询
    @Select("SELECT \n" +
            "  supply_id AS supplyId,\n" +
            "  supply_name AS supplyName,\n" +
            "  supply_type AS supplyType,\n" +
            "  brand,\n" +
            "  price,\n" +
            "  stock,\n" +
            "  status,\n" +
            "  description,\n" +
            "  supply_image AS supplyImage,\n" +
            "  create_date AS createDate\n" +
            "FROM pet_supplies \n" +
            "WHERE \n" +
            "  supply_name LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  supply_type LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  brand LIKE CONCAT('%', #{keyword}, '%') OR\n" +
            "  status LIKE CONCAT('%', #{keyword}, '%')")
    List<PetSupplies> findPetSuppliesByKeyword(String keyword);

}
