package com.ambow.springboot_adoption.service;

import com.ambow.springboot_adoption.vo.OrderItem;
import com.ambow.springboot_adoption.vo.PetSupplies;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface PetSuppliesService {
    //查全部
    //List<PetSupplies> selectAllPetSupplies();
    IPage<PetSupplies> selectAllPetSuppliesPage(int pageNum, int pageSize);
    //根据订单id查询订单详情
    PetSupplies getSupplyById(Integer supplyId);
    //添加
    int addPetSupplies(PetSupplies petSupplies);
    //修改
    int updatePetSupplies(PetSupplies petSupplies);
    //删除
    int deletePetSupplies(Integer supplyId);
    //模糊查询
    List<PetSupplies> findPetSuppliesByKeyword(String keyword);
}
